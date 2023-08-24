package com.tenius.sns.controller.advice;

import com.tenius.sns.dto.ErrorResponse;
import com.tenius.sns.exception.InputValueException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class CustomRestAdvice {
    /**
     * 데이터 검증 (Validation) 도중 발생한 예외 처리
     * @param e MethodArgumentNotValidException
     * @return ResponseEntity (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        BindingResult bindingResult=e.getBindingResult();
        ErrorResponse errorResponse=new ErrorResponse();
        StringBuilder message=new StringBuilder();

        bindingResult.getFieldErrors().forEach((fieldError)->{
            message.append(fieldError.getField()+":"+fieldError.getDefaultMessage()+". ");
        });
        errorResponse.putItem("message",message.toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getResponse());
    }

    /**
     * DB 처리 도중 발생한, 데이터 무결성 위반 예외 처리
     * @param e DataIntegrityViolationException
     * @return ResponseEntity (400)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message","Data Integrity Violation Exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getResponse());
    }

    /**
     * DB 처리 도중 발생한, 데이터 접근 예외 처리
     * @param e DataAccessException
     * @return ResponseEntity (400)
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String,Object>> handleDataAccessException(DataAccessException e){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message","Data Access Exception");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse.getResponse());
    }

    /**
     * 로직 처리에 필요한 데이터가 존재하지 않는 경우 발생하는 예외 처리
     * @param e EntityNotFoundException, EmptyResultDataAccessException
     * @return ResponseEntity (404)
     */
    @ExceptionHandler({
            EntityNotFoundException.class,
            EmptyResultDataAccessException.class
    })
    public ResponseEntity<Map<String,Object>> handleNoSuchElementException(Exception e){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message","No Such Element Exception");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse.getResponse());
    }

    /**
     * 입력값이 잘못되었을 때 발생하는 예외 처리
     * @param e InputValueException
     * @return ResponseEntity
     */
    @ExceptionHandler(InputValueException.class)
    public ResponseEntity<Map<String, Object>> handleInputValueException(InputValueException e){
        ErrorResponse errorResponse=new ErrorResponse();
        errorResponse.putItem("message", e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(errorResponse.getResponse());
    }
}
