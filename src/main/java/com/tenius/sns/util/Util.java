package com.tenius.sns.util;

import com.tenius.sns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.security.SecureRandom;

@Component
public class Util {
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final SecureRandom RANDOM = new SecureRandom();
    private final int UID_LENGTH=16;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String generateUid() {
        String uid;
        do {
            StringBuilder sb = new StringBuilder(UID_LENGTH);
            for (int i = 0; i < UID_LENGTH; i++) {
                sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            }
            uid = sb.toString();
        } while (userRepository.existsById(uid));
        return uid;
    }
}
