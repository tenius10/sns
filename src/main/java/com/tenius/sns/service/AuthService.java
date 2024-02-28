package com.tenius.sns.service;

import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;


public interface AuthService {
    UserInfoDTO registerUser(SignUpRequestDTO signUpRequestDTO) throws InputValueException;
    boolean isTokenInBlacklist(String token) throws TokenException;
    void registerTokenInBlacklist(String token, String reason);
}
