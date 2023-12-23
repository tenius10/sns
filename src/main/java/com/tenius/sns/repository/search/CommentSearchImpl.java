package com.tenius.sns.repository.search;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CommentSearchImpl extends QuerydslRepositorySupport implements CommentSearch {
    public CommentSearchImpl(){
        super(Comment.class);
    }
    @Override
    public PageResponseDTO<CommentWithStatusDTO> search(Long pno, PageRequestDTO pageRequestDTO){
        Pageable pageable=pageRequestDTO.getPageable();
        pageable= PageRequest.of(0, pageable.getPageSize()+1, pageable.getSort());
        LocalDateTime pivot=pageRequestDTO.getCursor()!=null?
                pageRequestDTO.getCursor().getRegDate(): null;

        //Q 도메인
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QCommentStatus commentStatus=QCommentStatus.commentStatus;

        //comment 테이블과 user_info 테이블, comment_status 테이블 LEFT JOIN
        JPQLQuery<Comment> query=from(comment)
                .leftJoin(userInfo)
                .on(comment.writer.eq(userInfo))
                .leftJoin(commentStatus)
                .on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment);

        //where 설정
        query.where(comment.post.pno.eq(pno));

        if(pivot!=null){
            if(pageable.getSort().getOrderFor("regDate").isDescending()){
                query.where(comment.regDate.before(pivot));  //최신순
            }
            else{
                query.where(comment.regDate.after(pivot));  //등록순
            }
        }

        //페이징 설정
        this.getQuerydsl().applyPagination(pageable, query);

        List<CommentWithStatusDTO> dtoList=query.select(Projections.bean(CommentWithStatusDTO.class,
                comment.cno,
                comment.content,
                comment.post.pno.as("pno"),
                comment.regDate,
                comment.modDate,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                commentStatus.count().as("likeCount")
        )).fetch();

        pageable=pageRequestDTO.getPageable();
        boolean hasNext=false;
        if(dtoList.size()>pageable.getPageSize()){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<CommentWithStatusDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public Optional<CommentWithStatusDTO> findByIdWithAll(Long cno){
        //Q 도메인
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QCommentStatus commentStatus=QCommentStatus.commentStatus;

        //comment 테이블과 user_info 테이블, comment_status 테이블 LEFT JOIN
        JPQLQuery<Comment> query=from(comment)
                .leftJoin(userInfo)
                .on(comment.writer.eq(userInfo))
                .leftJoin(commentStatus)
                .on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment);

        //where 설정
        query.where(comment.cno.eq(cno));

        List<CommentWithStatusDTO> dtoList=query.select(Projections.bean(CommentWithStatusDTO.class,
                comment.cno,
                comment.content,
                comment.post.pno.as("pno"),
                comment.regDate,
                comment.modDate,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer"),
                commentStatus.count().as("likeCount")
        )).fetch();

        return Optional.ofNullable(dtoList.size()>0?dtoList.get(0):null);
    }
}
