package com.tenius.sns.service;

import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class AuthServiceTests {
    @Autowired
    private AuthService authService;

    @Test
    public void testRegisterUser(){
        SignUpRequestDTO signUpRequestDTO= SignUpRequestDTO.builder()
                .username("testuser3")
                .password("testpassword3")
                .nickname("밤양갱")
                .build();
        UserInfoDTO result=authService.registerUser(signUpRequestDTO);
        log.info(result);
    }
}
