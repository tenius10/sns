package com.tenius.sns.service;

import com.tenius.sns.dto.FollowDTO;
import com.tenius.sns.dto.PageResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@SpringBootTest
@Transactional
public class FollowServiceTests {
    @Autowired
    private FollowService followService;

    @Test
    public void testFollow(){
        String followerUid="LwDzsVcEdRv01hT5";
        String followeeUid="JIr7YvhxDLO2tqCt";
        followService.follow(followerUid, followeeUid);
    }
    @Test
    public void testUnfollow(){
        String followerUid="OaEajAEcfu9HkKJr";
        String followeeUid="tRSH7Wlqn1PsPKRL";
        followService.unfollow(followerUid, followeeUid);
    }

    @Test
    public void testReadFollowerPage(){
        String myUid="JIr7YvhxDLO2tqCt";
        String uid="LwDzsVcEdRv01hT5";
        String cursorUid=null;

        PageResponseDTO<FollowDTO> result=followService.readFollowerPage(cursorUid, uid, myUid);

        if(result.getContent()!=null){
            result.getContent().forEach(followDTO -> log.info(followDTO));
        }
        log.info("다음 페이지 여부 : "+result.isHasNext());
    }
    @Test
    public void testReadFollowingPage(){
        String myUid="LwDzsVcEdRv01hT5";
        String uid="n4Pa4fBASIC8Ska5";
        String cursorUid="LwDzsVcEdRv01hT5";

        PageResponseDTO<FollowDTO> result=followService.readFollowingPage(cursorUid, uid, myUid);

        if(result.getContent()!=null){
            result.getContent().forEach(followDTO -> log.info(followDTO));
        }
        log.info("다음 페이지 여부 : "+result.isHasNext());
    }
}
