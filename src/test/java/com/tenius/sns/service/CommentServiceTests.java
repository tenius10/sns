package com.tenius.sns.service;

import com.tenius.sns.dto.CommentDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class CommentServiceTests {
    @Autowired
    private CommentService commentService;

    @Test
    public void testRegister(){
        String uid="x3SzQoEkSRwDnspp";
        Long pno=1L;
        CommentDTO commentDTO=CommentDTO.builder()
                .content("서비스 댓글 등록 테스트")
                .build();
        CommentDTO result=commentService.register(commentDTO, pno, uid);
        log.info(result);
    }
    @Test
    public void testReadOne(){
        Long cno=2L;
        CommentDTO result=commentService.readOne(cno);
        log.info(result);
    }
    @Test
    public void testModify(){
        Long cno=1L;
        CommentDTO commentDTO=CommentDTO.builder().content("댓글 수정 테스트").build();
        CommentDTO result=commentService.modify(cno, commentDTO);
        log.info(result);
    }
    @Test
    public void testRemove(){
        Long cno=1L;
        commentService.remove(cno);
    }

    @Test
    public void testLike(){
        Long cno=1L;
        String uid="Lt2T09Awufed3wop";
        CommentDTO result=commentService.like(cno,uid);
        log.info(result);
    }
    @Test
    public void testUnlike(){
        Long cno=1L;
        String uid="FEk9MRBg67YpClpU";
        CommentDTO result=commentService.unlike(cno,uid);
        log.info(result);
    }
}
