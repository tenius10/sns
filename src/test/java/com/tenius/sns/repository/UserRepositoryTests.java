package com.tenius.sns.repository;

import com.tenius.sns.domain.User;
import com.tenius.sns.domain.UserInfo;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Log4j2
@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;

    @Test
    public void testInsert(){
        String uid= "mILa4I9Yzp2nkI5g";

        User user=User.builder()
                .uid(uid)
                .username("testuser1")
                .password(encoder.encode("testpassword1"))
                .build();
        UserInfo userInfo= UserInfo.builder()
                .uid(uid)
                .nickname("밤샘코딩")
                .user(user)
                .build();
        user.initUserInfo(userInfo);

        User result=userRepository.save(user);
        log.info(result);
    }
    @Test
    public void testDelete(){
        String uid="sDwfiFh22ILucuPj";
        userRepository.deleteById(uid);
    }
}
