package com.tenius.sns.repository;

import com.tenius.sns.domain.PostLike;
import com.tenius.sns.domain.PostLikeKey;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Log4j2
public class PostLikeRepositoryTests {
    @Autowired
    private PostLikeRepository postLikeRepository;

    @Test
    public void testInsert(){
        PostLike postLike = PostLike.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        PostLike result= postLikeRepository.save(postLike);
        log.info(result);
    }
    @Test
    public void testDelete(){
        PostLikeKey key= PostLikeKey.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        postLikeRepository.deleteById(key);
    }
    @Test
    public void testExists(){
        PostLikeKey key=PostLikeKey.builder()
                .pno(1L)
                .uid("ugFyVwlT2nqdZH6F")
                .build();
        Boolean result= postLikeRepository.existsById(key);
        log.info("존재 여부: "+result);
    }
}
