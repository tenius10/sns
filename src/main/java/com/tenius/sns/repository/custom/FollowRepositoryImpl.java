package com.tenius.sns.repository.custom;

import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.UserInfoService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.tenius.sns.domain.QFollow.follow;
import static com.tenius.sns.domain.QUserInfo.userInfo;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDTO<FollowDTO> findAllFollowersByFolloweeUid(PageRequestDTO pageRequestDTO, String followeeUid, String myUid){
        int pageSize=pageRequestDTO.getSize();
        Long cursor= pageRequestDTO.getCursor();

        // 쿼리 설정
        JPQLQuery<UserInfo> query=queryFactory
                .select(userInfo)
                .from(userInfo)
                .where(follow.followeeUid.eq(followeeUid))
                .leftJoin(follow).on(follow.followerUid.eq(userInfo.uid));

        // 페이징 설정 (최신순)
        if(cursor!=null){
            query.where(follow.fno.lt(cursor));
        }
        query.orderBy(follow.fno.desc()).limit(pageSize+1);

        // 쿼리 실행
        List<UserInfo> userInfoList=query.fetch();
        List<String> uidList=userInfoList.stream().map((userInfo1)->userInfo1.getUid()).collect(Collectors.toList());
        List<String> myFollwingList=queryFactory
                .select(follow.followeeUid)
                .from(follow)
                .where(follow.followerUid.eq(myUid),follow.followeeUid.in(uidList))
                .fetch();

        // Entity를 DTO로 변환
        List<FollowDTO> dtoList=userInfoList.stream().map((userInfo1)->{
            UserInfoDTO userInfoDTO= UserInfoService.entityToDTO(userInfo1);
            FollowDTO followDTO=FollowDTO.followDTOBuilder()
                    .userInfoDTO(userInfoDTO)
                    .isFollowed(myFollwingList.contains(userInfo1.getUid()))
                    .build();
            return followDTO;
        }).collect(Collectors.toList());

        boolean hasNext=false;
        if(dtoList.size()>pageSize){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<FollowDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public PageResponseDTO<FollowDTO> findAllFollowingsByFollowerUid(PageRequestDTO pageRequestDTO, String followerUid, String myUid){
        int pageSize= pageRequestDTO.getSize();
        Long cursor=pageRequestDTO.getCursor();

        // 쿼리 설정
        JPQLQuery<UserInfo> query=queryFactory
                .select(userInfo)
                .from(userInfo)
                .where(follow.followerUid.eq(followerUid))
                .leftJoin(follow).on(follow.followeeUid.eq(userInfo.uid));

        // 페이징 설정 (최신순)
        if(cursor!=null){
            query.where(follow.fno.lt(cursor));
        }
        query.orderBy(follow.fno.desc())
                .limit(pageSize+1);

        // 쿼리 실행
        List<UserInfo> userInfoList=query.fetch();
        List<String> uidList=userInfoList.stream().map((userInfo1)->userInfo1.getUid()).collect(Collectors.toList());
        List<String> myFollowingList=queryFactory
                .select(follow.followeeUid)
                .from(follow)
                .where(follow.followerUid.eq(myUid), follow.followeeUid.in(uidList))
                .fetch();

        // Entity를 DTO로 변환
        List<FollowDTO> dtoList=userInfoList.stream().map((userInfo1)->{
            UserInfoDTO userInfoDTO=UserInfoService.entityToDTO(userInfo1);
            FollowDTO followDTO= FollowDTO.followDTOBuilder()
                    .userInfoDTO(userInfoDTO)
                    .isFollowed(myFollowingList.contains(userInfo1.getUid()))
                    .build();
            return followDTO;
        }).collect(Collectors.toList());

        boolean hasNext=false;
        if(dtoList.size()>pageSize){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<FollowDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }
}
