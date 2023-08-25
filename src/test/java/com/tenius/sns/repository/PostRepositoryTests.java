package com.tenius.sns.repository;

import com.tenius.sns.domain.Post;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithCountDTO;
import com.tenius.sns.dto.PostDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@Transactional
@SpringBootTest
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void testInsert(){
        String uid="ugFyVwlT2nqdZH6F";
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        IntStream.rangeClosed(1,100).forEach(i->{
            Post post=Post.builder()
                    .content("내용..."+i)
                    .writer(userInfo)
                    .views(0)
                    .build();
            Post result=postRepository.save(post);
            log.info(result);
        });
    }
    @Test
    public void testRead(){
        Long pno=2L;
        Post result=postRepository.findById(pno).orElseThrow();
        log.info(result);
    }
    @Test
    public void testUpdate(){
        Long pno=100L;
        Post post=postRepository.findById(pno).orElseThrow();
        Post newPost=new Post(post, "수정된 내용");
        Post result=postRepository.save(newPost);

        log.info(result);
    }
    @Test
    public void testDelete(){
        Long pno=2L;
        postRepository.deleteById(pno);
    }
    @Test
    public void testPagingByOffset(){
        Pageable pageable= PageRequest.of(0,10, Sort.by("pno").descending());
        Page<Post> result=postRepository.findAll(pageable);

        log.info("전체 데이터 개수: "+result.getTotalElements());
        log.info("전체 페이지 개수: "+result.getTotalPages());
        log.info("현재 페이지 번호: "+result.getNumber());
        log.info("페이지 크기: "+result.getSize());

        List<Post> postList=result.getContent();
        postList.forEach(post->log.info(post));
    }
    @Test
    public void testPagingByCursor(){
        Long pivot=10L;
        Post post=postRepository.findById(pivot).orElseThrow();
        PageResponseDTO<PostWithCountDTO> result =postRepository.search(PageRequestDTO.builder()
                .cursor(modelMapper.map(post, PostDTO.class))
                .build()
        );
        log.info("다음 페이지 존재 여부: "+result.isHasNext());
        result.getContent().forEach(postCommentCountDTO->log.info(postCommentCountDTO));
    }
}
