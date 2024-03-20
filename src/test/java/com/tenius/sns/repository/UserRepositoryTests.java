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

        UserInfo userInfo= UserInfo.builder()
                .uid(uid)
                .nickname("밤샘코딩")
                .intro("오늘 집중이 안 되는 날이네요...뀽..ㅠㅡㅜ")
                .build();
        User user=User.builder()
                .username("testuser1")
                .password(encoder.encode("testpassword1"))
                .userInfo(userInfo)
                .build();

        userRepository.save(user);
    }
    @Test
    public void testDelete(){
        String uid="mILa4I9Yzp2nkI5g";
        userRepository.deleteById(uid);
    }
}
