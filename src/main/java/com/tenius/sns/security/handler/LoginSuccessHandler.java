package com.tenius.sns.security.handler;

import com.google.gson.Gson;
import com.tenius.sns.exception.ErrorResponse;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.UserInfoService;
import com.tenius.sns.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserInfoService userInfoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Login Success Handler.........");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);

        //Access token, Refresh token 추가
        ResponseCookie accessTokenCookie= jwtUtil.getAccessTokenCookie(authentication);
        ResponseCookie refreshTokenCookie= jwtUtil.getRefreshTokenCookie(authentication);

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        try {
            //응답 본문 구성
            Gson gson = new Gson();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if(userDetails==null){
                throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
            }
            UserInfoDTO userInfoDTO=userInfoService.read(userDetails.getUid());

            ErrorResponse errorResponse=new ErrorResponse();
            errorResponse.putItem("message","Login Successful");
            errorResponse.putItem("user", userInfoDTO);

            response.getWriter().write(gson.toJson(errorResponse.getResponse()));
        }
        catch(TokenException e){
            log.error("TokenException: "+e.getMessage());
            e.sendResponseError(response);
        }
    }
}
