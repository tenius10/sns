package com.tenius.sns.service;

import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;


public interface AuthService {
    UserInfoDTO registerUser(SignUpRequestDTO signUpRequestDTO);
    boolean isTokenInBlacklist(String token);
    void registerTokenInBlacklist(String token, String reason);
}
