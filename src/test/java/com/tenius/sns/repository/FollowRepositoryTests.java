package com.tenius.sns.repository;

import com.tenius.sns.domain.Follow;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@SpringBootTest
public class FollowRepositoryTests {
    @Autowired
    private FollowRepository followRepository;

    @Test
    public void testInsert(){
        String followerUid="n4Pa4fBASIC8Ska5";
        String followeeUid="LwDzsVcEdRv01hT5";
        Follow follow=Follow.builder()
                .followerUid(followerUid)
                .followeeUid(followeeUid).build();
        followRepository.save(follow);
    }
    @Test
    @Transactional
    public void testDelete(){
        String followerUid="aaa";
        String followeeUid="bbb";
        followRepository.deleteByFollowerUidAndFolloweeUid(followerUid, followeeUid);
    }
    @Test
    public void testCount(){
        String myUid="LwDzsVcEdRv01hT5";
        long followerCount=followRepository.countByFolloweeUid(myUid);
        long followingCount=followRepository.countByFollowerUid(myUid);
        log.info("팔로워 수 : "+followerCount);
        log.info("팔로잉 수 : "+followingCount);
    }
}
