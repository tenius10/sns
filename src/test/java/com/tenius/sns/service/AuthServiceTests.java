package com.tenius.sns.service;

import com.tenius.sns.dto.SignUpRequestDTO;
import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.InputValueException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
public class AuthServiceTests {
    @Autowired
    private AuthService authService;

    @Test
    public void testRegisterUser(){
        SignUpRequestDTO signUpRequestDTO= SignUpRequestDTO.builder()
                .username("testuser1")
                .password("testpassword1")
                .nickname("밤샘코딩")
                .build();
        UserInfoDTO result=authService.registerUser(signUpRequestDTO);
        log.info(result);
    }
}
