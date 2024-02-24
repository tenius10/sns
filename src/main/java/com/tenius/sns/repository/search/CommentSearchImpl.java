package com.tenius.sns.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.CommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        QStorageFile storageFile=QStorageFile.storageFile;

        //쿼리 실행
        JPQLQuery<Comment> query=from(comment)
                .where(comment.post.pno.eq(pno))
                .leftJoin(userInfo).on(userInfo.eq(comment.writer))
                .leftJoin(storageFile).on(storageFile.eq(userInfo.profileImage))
                .leftJoin(commentStatus).on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment);
        //pivot 설정
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
        List<Tuple> tupleList=query.select(comment, userInfo, commentStatus.countDistinct()).fetch();

        //Entity를 DTO로 변환
        List<CommentWithStatusDTO> dtoList=tupleList.stream().map((tuple)->{
            Comment comment1=tuple.get(comment);
            UserInfo userInfo1=tuple.get(userInfo);
            Long likeCount=tuple.get(commentStatus.countDistinct());

            CommentDTO commentDTO=CommentService.entityToDTO(comment1, userInfo1);
            CommentWithStatusDTO commentWithStatusDTO=CommentWithStatusDTO.commentWithStatusDTOBuilder()
                    .commentDTO(commentDTO)
                    .likeCount(likeCount)
                    .build();
            return commentWithStatusDTO;
        }).collect(Collectors.toList());

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
        QStorageFile storageFile=QStorageFile.storageFile;

        //쿼리 실행
        List<Tuple> tupleList=from(comment)
                .where(comment.cno.eq(cno))
                .leftJoin(userInfo).on(userInfo.eq(comment.writer))
                .leftJoin(storageFile).on(storageFile.eq(userInfo.profileImage))
                .leftJoin(commentStatus).on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment)
                .select(comment, userInfo, commentStatus.countDistinct())
                .fetch();

        //Entity를 DTO로 변환
        List<CommentWithStatusDTO> dtoList=tupleList.stream().map(tuple->{
            Comment comment1=tuple.get(comment);
            UserInfo userInfo1=tuple.get(userInfo);
            Long likeCount=tuple.get(commentStatus.countDistinct());

            CommentDTO commentDTO= CommentService.entityToDTO(comment1, userInfo1);
            CommentWithStatusDTO commentWithStatusDTO=CommentWithStatusDTO.commentWithStatusDTOBuilder()
                    .commentDTO(commentDTO)
                    .likeCount(likeCount)
                    .build();
            return commentWithStatusDTO;
        }).collect(Collectors.toList());

        return Optional.ofNullable(dtoList.size()>0?dtoList.get(0):null);
    }
}
