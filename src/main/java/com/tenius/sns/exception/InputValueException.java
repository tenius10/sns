package com.tenius.sns.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class InputValueException extends RuntimeException {
    INPUT_VALUE_ERROR inputValueError;

    @Getter
    public enum INPUT_VALUE_ERROR{
        DUPLICATE_USERNAME(HttpStatus.CONFLICT, "이미 존재하는 아이디입니다."),
        DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
        DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다.");

        private HttpStatus status;
        private String message;

        INPUT_VALUE_ERROR(HttpStatus status, String message){
            this.status=status;
            this.message=message;
        }
    }

    public InputValueException(INPUT_VALUE_ERROR error){
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
