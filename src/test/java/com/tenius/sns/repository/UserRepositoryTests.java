package com.tenius.sns.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRepositoryTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testDelete(){
        String uid="UFPJ1P71lyJM8xNA";
        userRepository.deleteById(uid);
    }
}
