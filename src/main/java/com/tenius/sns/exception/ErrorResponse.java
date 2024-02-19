package com.tenius.sns.exception;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class ErrorResponse {
    private Map<String, Object> response;

    public ErrorResponse(){
        response=new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        response.put("time",LocalDateTime.now().format(formatter));
    }

    public void putItem(String key, Object value){
        response.put(key, value);
    }
}
