package com.tenius.sns.service;

import com.tenius.sns.dto.UserInfoDTO;


public interface UserInfoService {
    UserInfoDTO getUserInfo(String uid);
}

