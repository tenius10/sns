package com.tenius.sns.service;

import com.tenius.sns.dto.CommentDTO;
import com.tenius.sns.dto.CommentInputDTO;
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
        String uid="mILa4I9Yzp2nkI5g";
        Long pno=4L;
        CommentInputDTO commentInputDTO=CommentInputDTO.builder()
                .content("씁 오늘은 옵치가 잘 안 되네 끙...")
                .build();
        Long result=commentService.register(commentInputDTO, pno, uid);
        log.info("등록한 댓글의 ID : " + result);
    }
    @Test
    public void testRead(){
        Long cno=2L;
        CommentDTO result=commentService.read(cno);
        log.info(result);
    }
    @Test
    public void testModify(){
        Long cno=1L;
        CommentInputDTO commentInputDTO=CommentInputDTO.builder().content("댓글 수정 테스트").build();
        Long result=commentService.modify(cno, commentInputDTO);
        log.info("수정한 댓글의 ID : " + result);
    }
    @Test
    public void testRemove(){
        Long cno=1L;
        commentService.remove(cno);
    }

    @Test
    public void testLike(){
        Long cno=2L;
        String uid="OaEajAEcfu9HkKJr";
        Long result=commentService.like(cno,uid);
        log.info("좋아요 누른 댓글의 ID : " + result);
    }
    @Test
    public void testUnlike(){
        Long cno=2L;
        String uid="OaEajAEcfu9HkKJr";
        Long result=commentService.unlike(cno,uid);
        log.info("좋아요 취소한 댓글의 ID : " + result);
    }
}
