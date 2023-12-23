package com.tenius.sns.repository;

import com.tenius.sns.security.UserDetailsServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDelete(){
        String uid="ZEUkANTjpFsEt7Zy";
        userRepository.deleteById(uid);
    }
}
