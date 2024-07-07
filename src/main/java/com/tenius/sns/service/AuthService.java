package com.tenius.sns.service;

import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;


public interface AuthService {
    int MIN_USERNAME_LENGTH=8;
    int MAX_USERNAME_LENGTH=15;
    int MIN_PASSWORD_LENGTH=12;
    int MAX_PASSWORD_LENGTH=20;
    int MAX_EMAIL_LENGTH=100;

    String registerUser(SignUpRequestDTO signUpRequestDTO) throws InputValueException;
    boolean isTokenInBlacklist(String token) throws TokenException;
    void registerTokenInBlacklist(String token, String reason);
}
