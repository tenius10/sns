package com.tenius.sns.service;

import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;


public interface UserInfoService {
    int MIN_NICKNAME_LENGTH=2;
    int MAX_NICKNAME_LENGTH=10;
    int MAX_INTRO_LENGTH=300;

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
                .intro(userInfo.getIntro())
                .build();
        return result;
    }

    UserInfoDTO read(String uid);
    UserPageDTO readPage(String uid, String myUid);
    String modify(String uid, UserInfoDTO userInfoDTO) throws Exception;
}

