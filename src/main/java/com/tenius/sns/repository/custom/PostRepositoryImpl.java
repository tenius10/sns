package com.tenius.sns.repository.custom;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.PostService;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

import static com.tenius.sns.domain.QComment.comment;
import static com.tenius.sns.domain.QPost.post;
import static com.tenius.sns.domain.QPostLike.postLike;
import static com.tenius.sns.domain.QStorageFile.storageFile;
import static com.tenius.sns.domain.QUserInfo.userInfo;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String myUid){
        // 쿼리 실행 (Post, UserInfo, StorageFile(ProfileImage), CommentCount 가져오기)
        List<Tuple> tupleList=queryFactory
                .select(post, userInfo, comment.countDistinct())
                .from(post)
                .where(post.pno.eq(pno))
                .leftJoin(userInfo).on(userInfo.eq(post.writer))
                .leftJoin(storageFile).on(storageFile.eq(userInfo.profileImage))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post)
                .fetch();

        // 쿼리 실행 (likeCount 가져오기)
        List<Long> likeCountList=queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(postLike.pno.eq(pno))
                .fetch();
        long likeCount=0L;
        if(!likeCountList.isEmpty()){
            likeCount=likeCountList.get(0);
        }

        // 쿼리 실행 (isLiked 가져오기)
        List<Long> isLikedList=queryFactory
                .select(postLike.count())
                .from(postLike)
                .where(postLike.pno.eq(pno), postLike.uid.eq(myUid))
                .fetch();
        boolean isLiked=false;
        if(!isLikedList.isEmpty()){
            isLiked=isLikedList.get(0)>0;
        }


        // DTO 구성
        PostWithStatusDTO result=null;

        if(!tupleList.isEmpty()){
            Tuple tuple= tupleList.get(0);

            Post post1 = tuple.get(post);
            UserInfo userInfo1= tuple.get(userInfo);
            Long commentCount=tuple.get(comment.countDistinct());

            PostDTO postDTO= PostService.entityToDTO(post1, userInfo1);
            result=PostWithStatusDTO.postWithStatusDTOBuilder()
                    .postDTO(postDTO)
                    .commentCount(commentCount)
                    .likeCount(likeCount)
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(isLiked)
                    .build();
        }

        return Optional.ofNullable(result);
    }

    @Override
    public PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, SearchOptionDTO searchOptionDTO, String myUid){
        int pageSize = pageRequestDTO.getSize();
        String criteria = pageRequestDTO.getCriteria();
        Long cursor=pageRequestDTO.getCursor();
        String keyword= searchOptionDTO.getKeyword();
        String relatedUid= searchOptionDTO.getRelatedUid();

        // 쿼리 설정 (Post, UserInfo, StorageFile, commentCount 가져오기)
        JPQLQuery<Tuple> query=queryFactory
                .select(post, userInfo, comment.countDistinct())
                .from(post)
                .leftJoin(userInfo).on(post.writer.eq(userInfo))
                .leftJoin(storageFile).on(userInfo.profileImage.eq(storageFile))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post.pno);

        // 검색 옵션 설정 (특정 키워드 관련 게시글 검색)
        if(keyword!=null && !keyword.isEmpty()) {
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            booleanBuilder.or(post.content.contains(keyword));
            booleanBuilder.or(userInfo.nickname.contains(keyword));

            query.where(booleanBuilder);
        }

        // 검색 옵션 설정 (특정 유저 관련 게시글 검색)
        if(relatedUid!=null && !relatedUid.isEmpty()){
            // 본인이 작성 or 좋아요 클릭한 게시글
            query.leftJoin(postLike).on(postLike.pno.eq(post.pno));
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            booleanBuilder.or(post.writer.uid.eq(relatedUid));
            booleanBuilder.or(postLike.uid.eq(relatedUid));

            query.where(booleanBuilder);
        }

        // 페이징 설정 (기본 정렬 기준 : 최신순)
        if(cursor!=null){
            // 커서가 있는 경우
            if(criteria!=null && criteria.equals("created")){
                // 정렬 기준이 created(등록순)이면
                query.where(post.pno.gt(cursor)).orderBy(post.pno.asc());  // 등록순
            }
            else{
                // 정렬 기준이 없거나, created(등록순)이 아닌 경우 (디폴트)
                query.where(post.pno.lt(cursor)).orderBy(post.pno.desc());  // 최신순
            }
        }else{
            // 커서가 없는 경우
            query.orderBy(post.pno.desc());  // 최신순
        }

        // hasNext 확인을 위해 limit (size + 1)
        query.limit(pageSize+1);

        // 쿼리 실행 (Post, UserInfo, StorageFile, commentCount 가져오기)
        List<Tuple> tupleList=query.fetch();


        // 가져온 게시글의 ID 리스트
        List<Long> pnoList=tupleList.stream()
                .map(tuple->tuple.get(post).getPno())
                .collect(Collectors.toList());


        // 쿼리 실행 (likeCount 가져오기)
        Map<Long, Long> likeCountMap=new HashMap<>();  //<pno, likeCount>
        if(!pnoList.isEmpty()){
            List<Tuple> likeCountList=queryFactory
                    .select(postLike.pno, postLike.countDistinct())
                    .from(postLike)
                    .where(postLike.pno.in(pnoList))
                    .groupBy(postLike.pno)
                    .fetch();
            for(Tuple t: likeCountList){
                likeCountMap.put(t.get(postLike.pno), t.get(postLike.countDistinct()));
            }
        }

        // 쿼리 실행 (isLiked 가져오기)
        List<Long> isLikedList=queryFactory
                .select(postLike.pno)
                .from(postLike)
                .where(postLike.uid.eq(myUid), postLike.pno.in(pnoList))
                .fetch();


        // DTO 구성
        List<PostWithStatusDTO> dtoList=tupleList.stream().map(tuple->{
            Post post1= tuple.get(post);
            UserInfo userInfo1= tuple.get(userInfo);
            Long commentCount=tuple.get(comment.countDistinct());

            Long pno= post1.getPno();
            PostDTO postDTO=PostService.entityToDTO(post1, userInfo1);
            PostWithStatusDTO postWithStatusDTO=PostWithStatusDTO.postWithStatusDTOBuilder()
                    .postDTO(postDTO)
                    .commentCount(commentCount)
                    .likeCount(likeCountMap.getOrDefault(pno, 0L))
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(isLikedList.contains(pno))
                    .build();

            return postWithStatusDTO;
        }).collect(Collectors.toList());


        // 다음 페이지가 있는지 확인
        boolean hasNext=false;
        if(dtoList.size()>pageSize){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<PostWithStatusDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }
}