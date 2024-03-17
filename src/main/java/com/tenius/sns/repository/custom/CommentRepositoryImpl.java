package com.tenius.sns.repository.custom;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.CommentService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.tenius.sns.domain.QComment.comment;
import static com.tenius.sns.domain.QCommentStatus.commentStatus;
import static com.tenius.sns.domain.QStorageFile.storageFile;
import static com.tenius.sns.domain.QUserInfo.userInfo;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPQLQueryFactory queryFactory;

    @Override
    public PageResponseDTO<CommentWithStatusDTO> search(PageRequestDTO pageRequestDTO, Long pno, String myUid){
        int pageSize=pageRequestDTO.getSize();
        Long cursor= pageRequestDTO.getCursor();

        //쿼리 설정
        JPQLQuery<Tuple> query=queryFactory
                .select(comment, userInfo, commentStatus.countDistinct())
                .from(comment)
                .where(comment.post.pno.eq(pno))
                .leftJoin(userInfo, comment.writer)
                .leftJoin(storageFile, userInfo.profileImage)
                .leftJoin(commentStatus).on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment);

        //페이징 설정 (등록순)
        if(cursor!=null){
            query.where(comment.cno.gt(cursor));
        }
        // hasNext 를 확인하기 위해 limit (size + 1)
        query.orderBy(comment.cno.asc())
                .limit(pageSize+1);

        //쿼리 실행
        List<Tuple> tupleList=query.fetch();
        List<Long> cnoList=tupleList.stream().map((tuple)->tuple.get(comment).getCno()).collect(Collectors.toList());
        List<Long> likedCnos= queryFactory
                .select(commentStatus.cno)
                .from(commentStatus)
                .where(commentStatus.uid.eq(myUid), commentStatus.cno.in(cnoList), commentStatus.liked.isTrue())
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
        //쿼리 실행
        List<Tuple> tupleList=queryFactory
                .select(comment, userInfo, commentStatus.countDistinct())
                .from(comment)
                .where(comment.cno.eq(cno))
                .leftJoin(userInfo, comment.writer)
                .leftJoin(storageFile, userInfo.profileImage)
                .leftJoin(commentStatus).on(comment.cno.eq(commentStatus.cno), commentStatus.liked.isTrue())
                .groupBy(comment)
                .fetch();
        List<Boolean> likeList=queryFactory
                .select(commentStatus.liked)
                .from(commentStatus)
                .where(commentStatus.cno.eq(cno), commentStatus.uid.eq(myUid))
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
