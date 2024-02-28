package com.tenius.sns.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.CommentService;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CommentSearchImpl extends QuerydslRepositorySupport implements CommentSearch {
    public CommentSearchImpl(){
        super(Comment.class);
    }
    @Override
    public PageResponseDTO<CommentWithStatusDTO> search(PageRequestDTO pageRequestDTO, Long pno, String myUid){
        int pageSize=pageRequestDTO.getSize();
        Long cursor= pageRequestDTO.getCursor();

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

        //페이징 설정 (최신순)
        if(cursor!=null){
            query.where(comment.cno.lt(cursor));
        }
        // hasNext 를 확인하기 위해 limit (size + 1)
        query.orderBy(comment.cno.desc())
                .limit(pageSize+1);

        //쿼리 실행
        List<Tuple> tupleList=query.select(comment, userInfo, commentStatus.countDistinct()).fetch();
        List<Long> cnoList=tupleList.stream().map((tuple)->tuple.get(comment).getCno()).collect(Collectors.toList());
        List<Long> likedCnos= from(commentStatus)
                .where(commentStatus.uid.eq(myUid), commentStatus.cno.in(cnoList), commentStatus.liked.isTrue())
                .select(commentStatus.cno)
                .fetch();


        //Entity를 DTO로 변환
        List<CommentWithStatusDTO> dtoList=tupleList.stream().map((tuple)->{
            Comment comment1=tuple.get(comment);
            UserInfo userInfo1=tuple.get(userInfo);
            Long likeCount=tuple.get(commentStatus.countDistinct());

            CommentDTO commentDTO=CommentService.entityToDTO(comment1, userInfo1);
            CommentWithStatusDTO commentWithStatusDTO=CommentWithStatusDTO.commentWithStatusDTOBuilder()
                    .commentDTO(commentDTO)
                    .likeCount(likeCount)
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(likedCnos.contains(comment1.getCno()))
                    .build();
            return commentWithStatusDTO;
        }).collect(Collectors.toList());

        boolean hasNext=false;
        if(dtoList.size()>pageSize){
            dtoList.remove(dtoList.size()-1);
            hasNext=true;
        }

        return PageResponseDTO.<CommentWithStatusDTO>builder()
                .content(dtoList)
                .hasNext(hasNext)
                .build();
    }

    @Override
    public Optional<CommentWithStatusDTO> findByIdWithAll(Long cno, String myUid){
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
        List<Boolean> likeList=from(commentStatus)
                .where(commentStatus.cno.eq(cno), commentStatus.uid.eq(myUid))
                .select(commentStatus.liked)
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

        //owned와 liked 필드 채우기
        CommentWithStatusDTO result=null;
        if(dtoList.size()>0){
            result=dtoList.get(0);
            result.setOwned(result.getWriter().getUid().equals(myUid));
            result.setLiked(likeList.size()>0);
        }

        return Optional.ofNullable(result);
    }
}
