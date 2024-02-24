package com.tenius.sns.service;

import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.UserInfoDTO;


public interface UserInfoService {
    static UserInfoDTO entityToDTO(UserInfo userInfo){
        if(userInfo==null) return null;
        StorageFile file=userInfo.getProfileImage();
        String fileName= (file!=null)?
                FileService.getFileName(file.getUuid(), file.getFileName())
                : FileService.DEFAULT_PROFILE;
        UserInfoDTO result= UserInfoDTO.builder()
                .uid(userInfo.getUid())
                .nickname(userInfo.getNickname())
                .profileName(fileName)
                .build();
        return result;
    }

    UserInfoDTO read(String uid);
    UserInfoDTO modify(String uid, UserInfoDTO userInfoDTO) throws Exception;
}

