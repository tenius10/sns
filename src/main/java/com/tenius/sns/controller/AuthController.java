package com.tenius.sns.controller;

import com.google.gson.Gson;
import com.tenius.sns.exception.ErrorResponse;
import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.service.AuthService;
import com.tenius.sns.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @ApiOperation("회원가입")
    @PostMapping(value = "/signup", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO){
        UserInfoDTO userInfoDTO=authService.registerUser(signUpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfoDTO);
    }

    @ApiOperation("로그아웃")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //쿠키 없애기
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtil.getClearAccessTokenCookie().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtil.getClearRefreshTokenCookie().toString());

        //SecurityContext 비우기
        SecurityContextHolder.clearContext();

        //토큰 블랙리스트 등록
        String accessToken= jwtUtil.getAccessTokenFromCookies(request);
        String refreshToken= jwtUtil.getRefreshTokenFromCookies(request);

        authService.registerTokenInBlacklist(accessToken, jwtUtil.TOKEN_BLACKLIST_REASON);
        authService.registerTokenInBlacklist(refreshToken, jwtUtil.TOKEN_BLACKLIST_REASON);

        //응답 구성
        response.setStatus(HttpServletResponse.SC_OK);

        Gson gson = new Gson();
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message","Logout Successful");

        response.getWriter().write(gson.toJson(errorResponse.getResponse()));
    }

    @ApiOperation("토큰 재발급")
    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try{
            //Refresh Token 가져오기
            String refreshToken= jwtUtil.getRefreshTokenFromCookies(request);

            if(refreshToken==null){
                throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
            }

            //Refresh Token 이 유효하다면
            if(jwtUtil.validateToken(refreshToken) && !authService.isTokenInBlacklist(refreshToken)){
                //Refresh Token 만료까지 남은 시간 계산
                long expTime= jwtUtil.getExpirationFromJwtToken(refreshToken).getTime();
                long nowTime = System.currentTimeMillis();
                long diff = expTime - nowTime;

                //응답 구성
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_OK);

                //Access Token 재발급
                String username= jwtUtil.getUserNameFromJwtToken(refreshToken);
                ResponseCookie accessTokenCookie= jwtUtil.getAccessTokenCookie(username);
                response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

                //Refresh Token 만료까지 남은 시간이 적으면, Refresh Token 도 재발급
                if(diff < jwtUtil.REFRESH_TOKEN_REISSUE_MS){
                    ResponseCookie refreshTokenCookie= jwtUtil.getRefreshTokenCookie(username);
                    response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                }

                //응답 본문 구성
                Gson gson = new Gson();
                ErrorResponse errorResponse=new ErrorResponse();
                errorResponse.putItem("message", "Token Refresh Successful");

                response.getWriter().write(gson.toJson(errorResponse.getResponse()));
            }
        }
        catch(TokenException e){
            log.error("TokenException: "+e.getMessage());
            e.sendResponseError(response);
        }
    }
}
