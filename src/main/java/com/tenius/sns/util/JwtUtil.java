package com.tenius.sns.util;

import com.tenius.sns.exception.TokenException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


@Component
@Log4j2
public class JwtUtil {
    public final String ACCESS_TOKEN="access-token";
    public final String REFRESH_TOKEN="refresh-token";
    public final long REFRESH_TOKEN_REISSUE_MS=1000*60*60*24*7;  //7일

    @Value("${com.tenius.jwt.secret}")
    private String jwtSecret;
    private final int ACCESS_TOKEN_EXPIRATION_SEC=60*10;  //10분
    private final int REFRESH_TOKEN_EXPIRATION_SEC=60*60*24*10;  //10일

    public String getAccessTokenFromCookies(HttpServletRequest request) {
        return getTokenFromCookies(request, ACCESS_TOKEN);
    }
    public String getRefreshTokenFromCookies(HttpServletRequest request){
        return getTokenFromCookies(request, REFRESH_TOKEN);
    }

    public ResponseCookie getAccessTokenCookie(Authentication authentication){
        return getAccessTokenCookie(authentication.getName());
    }
    public ResponseCookie getAccessTokenCookie(String username){
        String jwt = generateTokenFromUsername(username, ACCESS_TOKEN_EXPIRATION_SEC);
        return ResponseCookie.from(ACCESS_TOKEN, jwt)
                .path("/")
                .maxAge(ACCESS_TOKEN_EXPIRATION_SEC)
                .httpOnly(true)
                .build();
    }


    public ResponseCookie getRefreshTokenCookie(Authentication authentication){
        return getRefreshTokenCookie(authentication.getName());
    }
    public ResponseCookie getRefreshTokenCookie(String username){
        String jwt = generateTokenFromUsername(username, REFRESH_TOKEN_EXPIRATION_SEC);
        return ResponseCookie.from(REFRESH_TOKEN, jwt)
                .path("/")
                .maxAge(REFRESH_TOKEN_EXPIRATION_SEC)
                .httpOnly(true)
                .build();
    }

    /**
     * 토큰 문자열에서 username을 추출하는 함수
     * @param token 토큰 문자열
     * @return username
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Date getExpirationFromJwtToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    /**
     * JWT 생성 함수
     * @param username 유저 이름
     * @param seconds 유효 시간
     * @return 토큰 문자열
     */
    private String generateTokenFromUsername(String username, int seconds) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusSeconds(seconds).toInstant()))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 검증 함수
     * @param token 토큰 문자열
     * @return 토큰의 페이로드
     * @throws JwtException
     */
    public boolean validateToken(String token) throws TokenException {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new TokenException(TokenException.TOKEN_ERROR.MALFORM);
        } catch (ExpiredJwtException e) {
            throw new TokenException(TokenException.TOKEN_ERROR.EXPIRED);
        } catch(SignatureException signatureException){
            throw new TokenException(TokenException.TOKEN_ERROR.BADSIGN);
        } catch (UnsupportedJwtException e) {
            throw new TokenException(TokenException.TOKEN_ERROR.UNSUPPORTED);
        } catch (IllegalArgumentException e) {
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
    }

    private String getTokenFromCookies(HttpServletRequest request, String tokenName){
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(tokenName)) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
