package com.tenius.sns.security.handler;

import com.google.gson.Gson;
import com.tenius.sns.dto.ErrorResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
        resp.setStatus(HttpServletResponse.SC_OK);

        //응답 본문 구성
        Gson gson = new Gson();
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message","Logout Successful");

        resp.getWriter().write(gson.toJson(errorResponse.getResponse()));
    }
}
