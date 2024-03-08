package com.tenius.sns.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InputValueException extends RuntimeException {
    ERROR inputValueError;

    @Getter
    public enum ERROR{
        DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
        DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
        DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
        NOT_FOUND_FILE(HttpStatus.BAD_REQUEST, "존재하지 않는 파일명입니다."),
        INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일명입니다."),
        UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 형식입니다."),
        NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 유저 ID입니다.");

        private HttpStatus status;
        private String message;

        ERROR(HttpStatus status, String message){
            this.status=status;
            this.message=message;
        }
    }

    public InputValueException(ERROR error){
        super(error.name());
        this.inputValueError=error;
    }

    public HttpStatus getStatus(){
        return inputValueError.getStatus();
    }
    public String getMessage(){
        return inputValueError.getMessage();
    }
}
