package com.tenius.sns.service;

import com.tenius.sns.domain.Post;
import com.tenius.sns.dto.*;
import com.tenius.sns.repository.PostRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Transactional
@SpringBootTest
public class UserInfoServiceTests {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private PostRepository postRepository;

    @Test
    public void testRead(){
        String uid="DxAMPRgqWjkfyVJn";
        UserInfoDTO result=userInfoService.read(uid);
        log.info(result);
    }
    @Test
    public void testUpdate(){
        String uid="Lt2T09Awufed3wop";
        UserInfoDTO userInfoDTO= UserInfoDTO.builder()
                .nickname("계란토스트")
                .intro("빨리 끝내고 옵치하러 가야징")
                .profileName(null)
                .build();
        try{
            String result=userInfoService.modify(uid, userInfoDTO);
            log.info("수정한 유저의 ID : " + result);
        }
        catch(Exception e){
            log.error(e);
        }
    }

    @Test
    public void testReadPage(){
        String myUid="JIr7YvhxDLO2tqCt";
        String uid="LwDzsVcEdRv01hT5";

        UserPageDTO result=userInfoService.readPage(uid, myUid);
        log.info("유저 정보 : "+result.getUserInfo());
        log.info("게시글 수 : "+result.getPostCount());
        log.info("팔로워 수 : "+result.getFollowerCount());
        log.info("팔로잉 수 : "+result.getFollowingCount());
        log.info("팔로우 여부 : "+result.isFollowed());
    }

    @Test
    public void testReadPostPage(){
        String uid="mILa4I9Yzp2nkI5g";
        Long cursor=null;
        PageRequestDTO pageRequestDTO= PageRequestDTO.builder().cursor(cursor).build();
        SearchOptionDTO searchOptionDTO=SearchOptionDTO.builder().relatedUid("mILa4I9Yzp2nkI5g").build();
        PageResponseDTO<PostWithStatusDTO> result =postRepository.search(pageRequestDTO, searchOptionDTO, uid);

        log.info("다음 페이지 존재 여부: "+result.isHasNext());
        result.getContent().forEach(postCommentCountDTO->log.info(postCommentCountDTO));
    }
}
