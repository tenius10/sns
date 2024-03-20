package com.tenius.sns.service;

import com.tenius.sns.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;


@SpringBootTest
@Log4j2
public class PostServiceTests {
    @Autowired
    private PostService postService;

    @Test
    public void testRegister(){
        String uid="OaEajAEcfu9HkKJr";
        PostInputDTO postInputDTO=PostInputDTO.builder()
                .content("테스트")
                .build();
        PostDTO result=postService.register(postInputDTO, uid);
        log.info(result);
    }
    @Test
    public void testReadOne(){
        Long pno=2L;
        PostDTO result=postService.readOne(pno);
        log.info(result);
    }
    @Test
    public void testView(){
        Long pno=81L;
        String uid="x3SzQoEkSRwDnspp";
        PostWithStatusDTO result=postService.view(pno, uid);
        log.info(result);
    }
    @Test
    public void testModify(){
        Long pno=2L;
        String uid="qGHLR09TjftDJKCA";
        PostInputDTO postInputDTO=PostInputDTO.builder()
                .content("서비스 수정 테스트")
                .build();
        try{
            PostWithStatusDTO result=postService.modify(pno, postInputDTO, uid);
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
        Long cursor=2L;
        String uid="OaEajAEcfu9HkKJr";
        PageRequestDTO pageRequestDTO=PageRequestDTO.builder().cursor(cursor).build();
        PageResponseDTO<PostWithStatusDTO> result=postService.readPage(pageRequestDTO, null, uid);

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
        PostInputDTO postInputDTO=PostInputDTO.builder()
                .content("서비스 계층에서 첨부파일과 함께 게시글 등록 테스트")
                .fileNames(List.of(UUID.randomUUID()+"_사진1.jpg"
                        ,UUID.randomUUID()+"_사진2.jpg"
                        ,UUID.randomUUID()+"_사진3.jpg"))
                .build();
        PostDTO result=postService.register(postInputDTO, uid);
        log.info(result);
    }
    @Test
    public void testViewWithImage(){
        Long pno=94L;
        String uid="x3SzQoEkSRwDnspp";
        PostWithStatusDTO result=postService.view(pno, uid);
        log.info(result);
    }
    @Test
    public void testModifyWithImage(){
        Long pno=94L;
        String uid="x3SzQoEkSRwDnspp";
        PostInputDTO postInputDTO=PostInputDTO.builder()
                .content("첨부파일 수정 (서비스 계층)")
                .fileNames(List.of(UUID.randomUUID()+"_수정된 사진1.jpg", UUID.randomUUID()+"_수정된 사진2.jpg"))
                .build();
        try{
            PostWithStatusDTO result=postService.modify(pno, postInputDTO, uid);
            log.info(result);
        } catch(Exception e){
            log.error(e.getMessage());
        }
    }
}
