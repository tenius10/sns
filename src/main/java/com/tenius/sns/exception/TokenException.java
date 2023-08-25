package com.tenius.sns.exception;

import com.google.gson.Gson;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

public class TokenException extends RuntimeException {
    TOKEN_ERROR tokenError;

    @Getter
    public enum TOKEN_ERROR{
        UNACCEPT(HttpStatus.UNAUTHORIZED,"Token is null or too short"),
        MALFORM(HttpStatus.UNAUTHORIZED,"Invalid Token"),
        EXPIRED(HttpStatus.UNAUTHORIZED, "Expired Token"),
        BADSIGN(HttpStatus.FORBIDDEN, "Bad Signatured Token"),
        UNSUPPORTED(HttpStatus.FORBIDDEN,"Unsupported Token");

        private HttpStatus status;
        private String message;

        TOKEN_ERROR(HttpStatus status, String message){
            this.status=status;
            this.message=message;
        }
    }

    public TokenException(TOKEN_ERROR error){
        super(error.name());
        this.tokenError=error;
    }

    public void sendResponseError(HttpServletResponse resp){
        resp.setStatus(tokenError.getStatus().value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson=new Gson();
        String responseBody=gson.toJson(Map.of("message",tokenError.getMessage(), "time", new Date()));

        try{
            resp.getWriter().println(responseBody);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
