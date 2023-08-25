package com.tenius.sns.repository;

import com.tenius.sns.domain.PostStatus;
import com.tenius.sns.domain.PostStatusKey;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
public class PostStatusRepositoryTests {
    @Autowired
    private PostStatusRepository postStatusRepository;

    @Test
    public void testInsert(){
        PostStatus postStatus=PostStatus.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        PostStatus result= postStatusRepository.save(postStatus);
        log.info(result);
    }
    @Test
    public void testDelete(){
        PostStatusKey key= PostStatusKey.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        postStatusRepository.deleteById(key);
    }
    @Test
    public void testExists(){
        PostStatusKey key=PostStatusKey.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        Boolean result= postStatusRepository.existsById(key);
        log.info("존재 여부: "+result);
    }
}
