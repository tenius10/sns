package com.tenius.sns.service;

import com.tenius.sns.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@Log4j2
public class PostServiceTests {
    @Autowired
    private PostService postService;

    @Test
    public void testRegister(){
        String uid="ugFyVwlT2nqdZH6F";
        PostDTO postDTO=PostDTO.builder()
                .content("서비스 등록 테스트")
                .views(0)
                .build();
        PostDTO result=postService.register(postDTO, uid);
        log.info(result);
    }
    @Test
    public void testReadOne(){
        Long pno=104L;
        PostCommentPageDTO result=postService.readOne(pno);
        log.info(result);
        result.getCommentPage().getContent().forEach(commentDTO -> log.info(commentDTO));
    }
    @Test
    public void testView(){
        Long pno=104L;
        PostCommentPageDTO result=postService.view(pno);
        log.info(result);
        result.getCommentPage().getContent().forEach(commentDTO -> log.info(commentDTO));
    }
    @Test
    public void testModify(){
        Long pno=1L;
        PostDTO postDTO=PostDTO.builder()
                .content("서비스 수정 테스트")
                .build();
        PostDTO result=postService.modify(pno, postDTO);
        log.info(result);
    }
    @Test
    public void testRemove(){
        Long pno=5L;
        postService.remove(pno);
    }
    @Test
    public void testPagingByCursor(){
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().build();
        PageResponseDTO<PostWithCountDTO> result=postService.readPage(pageRequestDTO);

        log.info(pageRequestDTO);
        result.getContent().forEach(postCommentCountDTO->log.info(postCommentCountDTO));
        log.info("커서: "+result.getCursor());
        log.info("다음 페이지 존재 여부: "+result.isHasNext());
    }

    @Test
    public void testLike(){
        Long pno=5L;
        String uid="ugFyVwlT2nqdZH6F";
        PostDTO result=postService.like(pno,uid);
        log.info(result);
    }
    @Test
    public void testUnlike(){
        Long pno=1L;
        String uid="bgzY3mS0KbElSTub";
        PostDTO result=postService.unlike(pno, uid);
        log.info(result);
    }
}
