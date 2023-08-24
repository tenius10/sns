package com.tenius.sns.service;

import com.tenius.sns.dto.UserInfoDTO;

import java.util.UUID;

public interface UserInfoService {
    UserInfoDTO getUserInfo(String uid);
}

