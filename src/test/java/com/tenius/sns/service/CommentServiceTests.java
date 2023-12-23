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
        String uid="qGHLR09TjftDJKCA";
        Long pno=29L;
        CommentDTO commentDTO=CommentDTO.builder()
                .content("하... 화귀는 진짜 레전드야...")
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
        Long cno=7L;
        String uid="UFPJ1P71lyJM8xNA";
        CommentDTO result=commentService.like(cno,uid);
        log.info(result);
    }
    @Test
    public void testUnlike(){
        Long cno=2L;
        String uid="bgzY3mS0KbElSTub";
        CommentDTO result=commentService.unlike(cno,uid);
        log.info(result);
    }
}
