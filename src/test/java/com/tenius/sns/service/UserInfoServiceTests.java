package com.tenius.sns.service;

import com.tenius.sns.dto.UserInfoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
public class UserInfoServiceTests {
    @Autowired
    private UserInfoService userInfoService;

    @Test
    public void testRead(){
        String uid="DxAMPRgqWjkfyVJn";
        UserInfoDTO result=userInfoService.read(uid);
        log.info(result);
    }
    @Test
    public void testUpdate(){
        String uid="DxAMPRgqWjkfyVJn";
        UserInfoDTO userInfoDTO= UserInfoDTO.builder()
                .nickname("고기만두")
                .profileName("Jfnoini32cnnxizu9c32_프로필 사진1.jpeg")
                .build();
        try{
            UserInfoDTO result=userInfoService.modify(uid, userInfoDTO);
            log.info(result);
        }
        catch(Exception e){
            log.error(e);
        }
    }
}
