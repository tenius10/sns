package com.tenius.sns.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.querydsl.jpa.JPAExpressions.select;

public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl(){
        super(Post.class);
    }

    @Override
    public PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, String uid){
        Pageable pageable=pageRequestDTO.getPageable();
        pageable=PageRequest.of(0, pageable.getPageSize()+1, pageable.getSort());
        LocalDateTime pivot=pageRequestDTO.getCursor()!=null?
                pageRequestDTO.getCursor().getRegDate(): null;

        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QPostStatus postStatus=QPostStatus.postStatus;

        //post 테이블과 user_info, comment 테이블 LEFT JOIN
        JPQLQuery<Post> query=from(post)
                .leftJoin(userInfo).on(post.writer.eq(userInfo))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post.pno);

        //pivot 설정
        if(pivot!=null){
            if(pageable.getSort().getOrderFor("regDate").isDescending()){
                query.where(post.regDate.before(pivot));  //최신순
            }
            else{
                query.where(post.regDate.after(pivot));  //등록순
            }
        }

        //페이징 설정
        this.getQuerydsl().applyPagination(pageable, query);

        List<PostWithStatusDTO> dtoList=query.select(Projections.bean(PostWithStatusDTO.class,
                post.pno,
                post.content,
                post.regDate,
                post.modDate,
                post.views,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                comment.count().as("commentCount")
        )).fetch();

        //각 게시글의 좋아요 개수 확인
        List<Long> pnoList=dtoList.stream().map((dto)->dto.getPno()).collect(Collectors.toList());
        List<Tuple> likeCountList=from(postStatus)
                .select(postStatus.pno, postStatus.count())
                .where(postStatus.pno.in(pnoList), postStatus.liked.isTrue())
                .groupBy(postStatus.pno)
                .fetch();
        Map<Long, Long> likeCountMap=new HashMap<>();  //<pno, likeCount>
        for(Tuple t: likeCountList){
            likeCountMap.put(t.get(postStatus.pno), t.get(postStatus.count()));
        }
        dtoList=dtoList.stream().map((dto)->{
            dto.setLikeCount(likeCountMap.getOrDefault(dto.getPno(), 0l));
            return dto;
        }).collect(Collectors.toList());


        //각 게시글에 좋아요를 눌렀는지 여부 확인
        List<Tuple> likedList= from(postStatus)
                .select(postStatus.pno, postStatus.liked)
                .where(postStatus.uid.eq(uid), postStatus.pno.in(pnoList), postStatus.liked.isTrue())
                .fetch();
        Map<Long, Boolean> likedMap=new HashMap<>();  //<pno, liked>
        for(Tuple t: likedList){
            likedMap.put(t.get(postStatus.pno), t.get(postStatus.liked));
        }

        dtoList=dtoList.stream().map((dto)->{
            dto.setOwned(dto.getWriter().getUid().equals(uid));
            dto.setLiked(likedMap.getOrDefault(dto.getPno(), false));
            return dto;
        }).collect(Collectors.toList());

        //pageable의 size를 원상복구한 후 반환
        pageable=pageRequestDTO.getPageable();
        boolean hasNext=false;
        if(dtoList.size()>pageable.getPageSize()){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<PostWithStatusDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String uid){
        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QPostStatus postStatus=QPostStatus.postStatus;

        //post 테이블과 user_info, comment 테이블 LEFT JOIN
        JPQLQuery<Post> query=from(post)
                .where(post.pno.eq(pno))
                .leftJoin(userInfo).on(userInfo.eq(post.writer))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post);

        List<PostWithStatusDTO> dtoList=query.select(Projections.bean(PostWithStatusDTO.class,
                post.pno,
                post.content,
                post.regDate,
                post.modDate,
                post.views,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                comment.count().as("commentCount")
        )).fetch();

        //likeCount 가져오기
        List<String> likedList=from(postStatus)
                .select(postStatus.uid)
                .where(postStatus.pno.eq(pno), postStatus.liked.isTrue())
                .fetch();

        PostWithStatusDTO result=null;
        if(dtoList.size()>0){
            result=dtoList.get(0);
            result.setOwned(result.getWriter().getUid().equals(uid));
            result.setLikeCount((long)likedList.size());
            result.setLiked(likedList.contains(uid));
        }

        return Optional.ofNullable(result);
    }
}
