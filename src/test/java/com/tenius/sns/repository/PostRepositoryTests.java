package com.tenius.sns.repository;

import com.tenius.sns.domain.Post;
import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Log4j2
@Transactional
@SpringBootTest
public class PostRepositoryTests {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    public void testInsert(){
        String uid="x3SzQoEkSRwDnspp";
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        IntStream.rangeClosed(1,10).forEach(i->{
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
        Long pno=45L;
        Post result=postRepository.findById(pno).orElseThrow();
        log.info(result.getWriter().getUid());
    }
    @Test
    public void testUpdate(){
        Long pno=100L;
        Post post=postRepository.findById(pno).orElseThrow();
        post.changeContent("수정된 내용");
        Post result=postRepository.save(post);

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
        String uid="x3SzQoEkSRwDnspp";
        Long cursor=45L;
        PageRequestDTO pageRequestDTO= PageRequestDTO.builder().cursor(cursor).build();

        PageResponseDTO<PostWithStatusDTO> result =postRepository.search(pageRequestDTO, null, uid);
        log.info("다음 페이지 존재 여부: "+result.isHasNext());
        result.getContent().forEach(postCommentCountDTO->log.info(postCommentCountDTO));
    }
    @Test
    public void testInsertWithFiles(){
        String uid="x3SzQoEkSRwDnspp";
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        Post post=Post.builder()
                .content("첨부파일 테스트")
                .writer(userInfo)
                .views(0)
                .build();
        //이미지 추가
        for(int i=0;i<3;i++){
            post.addFile(UUID.randomUUID().toString(), "테스트용 사진"+i+".jpg");
        }
        postRepository.save(post);
    }
    @Test
    public void testReadWithFiles(){
        Long pno=81L;
        Post post=postRepository.findByIdWithFiles(pno).orElseThrow();
        log.info(post);
        for(StorageFile file:post.getFiles()){
            log.info(file);
        }
    }
    @Test
    public void testCountByWriter(){
        String uid="n4Pa4fBASIC8Ska5";
        long result=postRepository.countByWriterUid(uid);
        log.info(result);
    }
}
