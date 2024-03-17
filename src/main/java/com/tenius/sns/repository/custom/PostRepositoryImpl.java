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
import static com.tenius.sns.domain.QPostStatus.postStatus;
import static com.tenius.sns.domain.QStorageFile.storageFile;
import static com.tenius.sns.domain.QUserInfo.userInfo;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, String writerUid, String myUid){
        int pageSize = pageRequestDTO.getSize();
        String criteria = pageRequestDTO.getCriteria();
        String keyword = pageRequestDTO.getKeyword();
        Long cursor=pageRequestDTO.getCursor();

        // 쿼리 설정
        JPQLQuery<Tuple> query=queryFactory
                .select(post, userInfo, comment.countDistinct())
                .from(post)
                .leftJoin(userInfo, post.writer)
                .leftJoin(storageFile, userInfo.profileImage)
                .leftJoin(comment.post, post)
                .groupBy(post.pno);

        // 유저 설정
        if(writerUid!=null && !writerUid.isEmpty()){
            query.leftJoin(postStatus).on(postStatus.pno.eq(post.pno), postStatus.liked.isTrue());
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            booleanBuilder.or(post.writer.uid.eq(writerUid));
            booleanBuilder.or(postStatus.uid.eq(writerUid));

            query.where(booleanBuilder);
        }

        // 키워드 설정
        if(keyword!=null && !keyword.isEmpty()) {
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            booleanBuilder.or(post.content.contains(keyword));
            booleanBuilder.or(userInfo.nickname.contains(keyword));

            query.where(booleanBuilder);
        }

        // 페이징 설정 (기본 정렬 기준 : 최신순)
        if(cursor!=null){
            if(criteria!=null && criteria.equals("created")){
                // 등록순
                query.where(post.pno.gt(cursor))
                        .orderBy(post.pno.asc());
            }
            else{
                // 최신순
                query.where(post.pno.lt(cursor))
                        .orderBy(post.pno.desc());
            }
        }else{
            // 최신순
            query.orderBy(post.pno.desc());
        }
        // hasNext 를 확인하기 위해 limit (size + 1)
        query.limit(pageSize+1);

        // 쿼리 실행 (게시글, 작성자, 댓글 개수 가져오기)
        List<Tuple> tupleList=query.fetch();

        // 쿼리 실행 (좋아요 정보, 각 게시글에 좋아요를 눌렀는지 여부 가져오기)
        Map<Long, Long> likeCountMap=new HashMap<>();  //<pno, likeCount>
        Map<Long, Boolean> likedMap=new HashMap<>();  //<pno, liked>

        List<Long> pnoList=tupleList.stream()
                .map(tuple->tuple.get(post).getPno())
                .collect(Collectors.toList());

        if(pnoList!=null && pnoList.size()>0){
            //좋아요 개수
            List<Tuple> likeCountList=queryFactory
                    .select(postStatus.pno, postStatus.countDistinct())
                    .from(postStatus)
                    .where(postStatus.pno.in(pnoList), postStatus.liked.isTrue())
                    .groupBy(postStatus.pno)
                    .fetch();
            for(Tuple t: likeCountList){
                likeCountMap.put(t.get(postStatus.pno), t.get(postStatus.countDistinct()));
            }
            //좋아요 눌렀는지 여부
            List<Tuple> likedList=queryFactory
                    .select(postStatus.pno, postStatus.liked)
                    .from(postStatus)
                    .where(postStatus.uid.eq(myUid), postStatus.pno.in(pnoList), postStatus.liked.isTrue())
                    .fetch();
            for(Tuple t: likedList){
                likedMap.put(t.get(postStatus.pno), t.get(postStatus.liked));
            }
        }


        //엔티티 튜플을 DTO 리스트로 변환
        List<PostWithStatusDTO> dtoList=tupleList.stream().map(tuple->{
            Post post1= tuple.get(post);
            UserInfo userInfo1= tuple.get(userInfo);
            long commentCount=tuple.get(comment.countDistinct());

            PostDTO postDTO=PostService.entityToDTO(post1, userInfo1);
            PostWithStatusDTO postWithStatusDTO=PostWithStatusDTO.postWithStatusDTOBuilder()
                    .postDTO(postDTO)
                    .commentCount(commentCount)
                    .likeCount(likeCountMap.getOrDefault(post1.getPno(), 0l))
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(likedMap.getOrDefault(post1.getPno(), false))
                    .build();

            return postWithStatusDTO;
        }).collect(Collectors.toList());

        //다음 페이지가 있는지 확인
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


    @Override
    public Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String myUid){
        //쿼리 실행 (게시글, 작성자, 댓글 개수, 좋아요 정보 가져오기)
        List<Tuple> tupleList=queryFactory
                .select(post, userInfo, comment.countDistinct())
                .from(post)
                .where(post.pno.eq(pno))
                .leftJoin(userInfo, post.writer)
                .leftJoin(storageFile, userInfo.profileImage)
                .leftJoin(comment.post, post)
                .groupBy(post)
                .fetch();

        List<String> likeList=queryFactory
                .select(postStatus.uid)
                .from(postStatus)
                .where(postStatus.pno.eq(pno), postStatus.liked.isTrue())
                .fetch();

        //Entity 튜플을 DTO 리스트로 변환
        List<PostWithStatusDTO> dtoList=tupleList.stream().map(tuple->{
            Post post1 = tuple.get(post);
            UserInfo userInfo1= tuple.get(userInfo);
            long commentCount=tuple.get(comment.countDistinct());

            PostDTO postDTO= PostService.entityToDTO(post1, userInfo1);
            PostWithStatusDTO postWithStatusDTO=PostWithStatusDTO.postWithStatusDTOBuilder()
                    .postDTO(postDTO)
                    .commentCount(commentCount)
                    .likeCount((long)likeList.size())
                    .build();

            return postWithStatusDTO;
        }).collect(Collectors.toList());

        //owned와 liked 필드 채우기
        PostWithStatusDTO result=null;
        if(dtoList.size()>0){
            result=dtoList.get(0);
            result.setOwned(result.getWriter().getUid().equals(myUid));
            result.setLiked(likeList.contains(myUid));
        }

        return Optional.ofNullable(result);
    }
}