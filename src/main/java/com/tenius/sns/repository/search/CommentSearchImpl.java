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

public class CommentSearchImpl extends QuerydslRepositorySupport implements CommentSearch {
    public CommentSearchImpl(){
        super(Comment.class);
    }
    @Override
    public PageResponseDTO<CommentWithCountDTO> search(Long pno, PageRequestDTO pageRequestDTO){
        Pageable pageable=pageRequestDTO.getPageable();
        pageable= PageRequest.of(0, pageable.getPageSize()+1, pageable.getSort());
        LocalDateTime pivot=pageRequestDTO.getPivot();

        //Q 도메인
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;

        //comment 테이블과 user_info 테이블 LEFT JOIN
        JPQLQuery<Comment> query=from(comment)
                .leftJoin(userInfo).on(comment.writer.eq(userInfo));

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

        List<CommentDTO> dtoList=query.select(Projections.bean(CommentDTO.class,
                comment.cno,
                comment.content,
                comment.post.pno.as("pno"),
                comment.regDate,
                comment.modDate,
                comment.likes,
                Projections.bean(UserInfoDTO.class,
                        userInfo.uid.as("uid"),
                        userInfo.nickname.as("nickname")
                ).as("writer")
        )).fetch();

        pageable=pageRequestDTO.getPageable();
        boolean hasNext=false;
        if(dtoList.size()>pageable.getPageSize()){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<CommentDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }
}
