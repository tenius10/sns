package com.tenius.sns.service;

import com.tenius.sns.dto.UserInfoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Transactional
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
        String uid="Lt2T09Awufed3wop";
        UserInfoDTO userInfoDTO= UserInfoDTO.builder()
                .nickname("계란토스트")
                .intro("빨리 끝내고 옵치하러 가야징")
                .profileName(null)
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
