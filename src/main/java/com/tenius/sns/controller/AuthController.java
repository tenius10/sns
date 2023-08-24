package com.tenius.sns.controller;

import com.google.gson.Gson;
import com.tenius.sns.dto.ErrorResponse;
import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.service.AuthService;
import com.tenius.sns.util.JwtUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
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
    @PostMapping("/signup")
    public ResponseEntity<UserInfoDTO> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO){
        UserInfoDTO userInfoDTO=authService.registerUser(signUpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userInfoDTO);
    }

    @ApiOperation("토큰 재발급")
    @PostMapping("/refresh")
    public void refreshToken(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try{
            //Refresh Token 가져오기
            String refreshToken= jwtUtil.getRefreshTokenFromCookies(req);
            if(refreshToken==null){
                throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
            }

            //Refresh Token 이 유효하다면
            if(jwtUtil.validateToken(refreshToken)){
                //Refresh Token 만료까지 남은 시간 계산
                long expTime= jwtUtil.getExpirationFromJwtToken(refreshToken).getTime();
                long nowTime = System.currentTimeMillis();
                long diff = expTime - nowTime;

                //응답 구성
                resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                resp.setStatus(HttpServletResponse.SC_OK);

                //Access Token 재발급
                String username= jwtUtil.getUserNameFromJwtToken(refreshToken);
                ResponseCookie accessTokenCookie= jwtUtil.getAccessTokenCookie(username);
                resp.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

                //Refresh Token 만료까지 남은 시간이 적으면, Refresh Token 도 재발급
                if(diff < jwtUtil.REFRESH_TOKEN_REISSUE_MS){
                    ResponseCookie refreshTokenCookie= jwtUtil.getRefreshTokenCookie(username);
                    resp.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
                }

                //응답 본문 구성
                Gson gson = new Gson();
                ErrorResponse errorResponse=new ErrorResponse();
                errorResponse.putItem("message", "Token Refresh Successful");

                resp.getWriter().write(gson.toJson(errorResponse.getResponse()));
            }
        }
        catch(TokenException e){
            log.error("TokenException: "+e.getMessage());
            e.sendResponseError(resp);
        }
    }
}
