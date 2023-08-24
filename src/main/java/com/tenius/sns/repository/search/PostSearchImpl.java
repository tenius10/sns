package com.tenius.sns.repository.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.Post;
import com.tenius.sns.domain.QComment;
import com.tenius.sns.domain.QPost;
import com.tenius.sns.domain.QUserInfo;
import com.tenius.sns.dto.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl(){
        super(Post.class);
    }

    @Override
    public PageResponseDTO<PostWithCountDTO> search(PageRequestDTO pageRequestDTO){
        Pageable pageable=pageRequestDTO.getPageable();
        pageable=PageRequest.of(0, pageable.getPageSize()+1, pageable.getSort());
        LocalDateTime pivot=pageRequestDTO.getPivot();

        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;

        //post 테이블과 user_info, comment 테이블 LEFT JOIN
        JPQLQuery<Post> query=from(post)
                .leftJoin(userInfo).on(post.writer.eq(userInfo))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post);

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

        List<PostWithCountDTO> dtoList=query.select(Projections.bean(PostWithCountDTO.class,
                post.pno,
                post.content,
                post.regDate,
                post.modDate,
                post.views,
                post.likes,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                comment.count().as("commentCount")
        )).fetch();

        pageable=pageRequestDTO.getPageable();
        boolean hasNext=false;
        if(dtoList.size()>pageable.getPageSize()){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<PostWithCountDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public Optional<PostWithCountDTO> findByIdWithAll(Long pno){
        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;

        //post 테이블과 user_info, comment 테이블 LEFT JOIN
        JPQLQuery<Post> query=from(post)
                .leftJoin(userInfo).on(post.writer.eq(userInfo))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post);

        query.where(post.pno.eq(pno));

        List<PostWithCountDTO> dtoList=query.select(Projections.bean(PostWithCountDTO.class,
                post.pno,
                post.content,
                post.regDate,
                post.modDate,
                post.views,
                post.likes,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                comment.count().as("commentCount")
        )).fetch();

        return Optional.ofNullable(dtoList.size()>0?dtoList.get(0):null);
    }
}
