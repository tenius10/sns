package com.tenius.sns.service;

import com.tenius.sns.domain.Follow;
import com.tenius.sns.dto.FollowDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.repository.FollowRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final UserInfoRepository userInfoRepository;
    private final FollowRepository followRepository;

    @Override
    public void follow(String followerUid, String followeeUid) throws InputValueException {
        //uid가 null이면 에러
        if(followerUid==null || followeeUid==null){
            throw new InputValueException(InputValueException.ERROR.NOT_FOUND_USER);
        }
        //followee는 존재하는 uid인지 확인 필요 (follower은 principal에서 빼온거니까 유효)
        if(!userInfoRepository.existsByUid(followeeUid)){
            throw new InputValueException(InputValueException.ERROR.NOT_FOUND_USER);
        }

        //팔로워와 팔로위의 uid가 모두 유효하다면 팔로우 엔티티 생성
        Follow follow= Follow.builder()
                .followerUid(followerUid)
                .followeeUid(followeeUid)
                .build();

        //아직 팔로우하지 않았다면, 팔로우 정보 저장
        if(!followRepository.existsByFollowerUidAndFolloweeUid(followerUid, followeeUid)){
            followRepository.save(follow);
        }
    }

    @Override
    public void unfollow(String followerUid, String followeeUid) {
        //언팔로우할 때는 그냥 DB에서 해당 데이터를 삭제하기만 하면 되니까 uid가 실제로 존재하는 유저인지 확인할 필요가 없음
        followRepository.deleteByFollowerUidAndFolloweeUid(followerUid, followeeUid);
    }
    @Override
    public PageResponseDTO<FollowDTO> readFollowerPage(String cursorUid, String uid, String myUid){
        Long cursor=null;
        if(cursorUid!=null && !cursorUid.isEmpty()){
            Follow follow=followRepository.findByFollowerUidAndFolloweeUid(cursorUid, uid).orElseThrow();
            cursor=follow.getFno();
        }
        PageRequestDTO pageRequestDTO= PageRequestDTO.builder()
                .cursor(cursor)
                .build();
        PageResponseDTO<FollowDTO> result=followRepository.findAllFollowersByFolloweeUid(pageRequestDTO, uid, myUid);
        return result;
    }
    @Override
    public PageResponseDTO<FollowDTO> readFollowingPage(String cursorUid, String uid, String myUid){
        Long cursor=null;
        if(cursorUid!=null && !cursorUid.isEmpty()){
            Follow follow=followRepository.findByFollowerUidAndFolloweeUid(uid, cursorUid).orElseThrow();
            cursor=follow.getFno();
        }
        PageRequestDTO pageRequestDTO= PageRequestDTO.builder()
                .cursor(cursor)
                .build();
        PageResponseDTO<FollowDTO> result=followRepository.findAllFollowingsByFollowerUid(pageRequestDTO, uid, myUid);
        return result;
    }
}
