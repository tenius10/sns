package com.tenius.sns.service;

import com.tenius.sns.domain.Post;
import com.tenius.sns.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@SpringBootTest
@Log4j2
public class PostServiceTests {
    @Autowired
    private PostService postService;

    @Test
    public void testRegister(){
        String uid="x3SzQoEkSRwDnspp";
        PostDTO postDTO=PostDTO.builder()
                .content("서비스 등록 테스트")
                .views(0)
                .build();
        PostDTO result=postService.register(postDTO, uid);
        log.info(result);
    }
    @Test
    public void testReadOne(){
        Long pno=81L;
        PostDTO result=postService.readOne(pno);
        log.info(result);
    }
    @Test
    public void testView(){
        Long pno=81L;
        String uid="x3SzQoEkSRwDnspp";
        PostCommentPageDTO result=postService.view(pno, uid);
        log.info(result);
        result.getCommentPage().getContent().forEach(commentDTO -> log.info(commentDTO));
    }
    @Test
    public void testModify(){
        Long pno=2L;
        String uid="qGHLR09TjftDJKCA";
        PostDTO postDTO=PostDTO.builder()
                .content("서비스 수정 테스트")
                .build();
        try{
            PostCommentPageDTO result=postService.modify(pno, postDTO, uid);
            log.info(result);
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }
    @Test
    public void testRemove(){
        Long pno=81L;
        try{
            postService.remove(pno);
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }
    @Test
    public void testPagingByCursor(){
        Long pivot=96L;
        String uid="x3SzQoEkSRwDnspp";
        PostDTO cursor=null;
        try{
            cursor=postService.readOne(pivot);
        }
        catch(Exception e){
            log.info(e.getMessage());
        }
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().cursor(cursor).build();
        PageResponseDTO<PostWithStatusDTO> result=postService.readPage(pageRequestDTO, uid);

        log.info(pageRequestDTO);
        result.getContent().forEach(postWithStatusDTO->log.info(postWithStatusDTO));
        log.info("다음 페이지 존재 여부: "+result.isHasNext());
    }

    @Test
    public void testLike(){
        Long pno=1L;
        String uid="Lt2T09Awufed3wop";
        PostWithStatusDTO result=postService.like(pno,uid);
        log.info(result);
    }
    @Test
    public void testUnlike(){
        Long pno=1L;
        String uid="ugFyVwlT2nqdZH6F";
        PostWithStatusDTO result=postService.unlike(pno, uid);
        log.info(result);
    }
    @Test
    public void testRegisterWithImages(){
        String uid="x3SzQoEkSRwDnspp";
        PostDTO postDTO=PostDTO.builder()
                .content("서비스 계층에서 첨부파일과 함께 게시글 등록 테스트")
                .fileNames(List.of(UUID.randomUUID()+"_사진1.jpg"
                        ,UUID.randomUUID()+"_사진2.jpg"
                        ,UUID.randomUUID()+"_사진3.jpg"))
                .build();
        PostDTO result=postService.register(postDTO, uid);
        log.info(result);
    }
    @Test
    public void testViewWithImage(){
        Long pno=94L;
        String uid="x3SzQoEkSRwDnspp";
        PostCommentPageDTO result=postService.view(pno, uid);
        log.info(result);
    }
    @Test
    public void testModifyWithImage(){
        Long pno=94L;
        String uid="x3SzQoEkSRwDnspp";
        PostDTO postDTO=PostDTO.builder()
                .content("첨부파일 수정 (서비스 계층)")
                .fileNames(List.of(UUID.randomUUID()+"_수정된 사진1.jpg", UUID.randomUUID()+"_수정된 사진2.jpg"))
                .build();
        try{
            PostCommentPageDTO result=postService.modify(pno, postDTO, uid);
            log.info(result);
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }
}
