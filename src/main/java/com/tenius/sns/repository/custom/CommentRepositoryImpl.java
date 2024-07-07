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
import static com.tenius.sns.domain.QCommentLike.commentLike;
import static com.tenius.sns.domain.QStorageFile.storageFile;
import static com.tenius.sns.domain.QUserInfo.userInfo;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPQLQueryFactory queryFactory;

    @Override
    public Optional<CommentWithStatusDTO> findByIdWithAll(Long cno, String myUid){
        // 쿼리 실행 (Comment, UserInfo, StorageFile, likeCount 가져오기)
        List<Tuple> tupleList=queryFactory
                .select(comment, userInfo, commentLike.countDistinct())
                .from(comment)
                .where(comment.cno.eq(cno))
                .leftJoin(userInfo).on(comment.writer.eq(userInfo))
                .leftJoin(storageFile).on(userInfo.profileImage.eq(storageFile))
                .leftJoin(commentLike).on(comment.cno.eq(commentLike.cno))
                .groupBy(comment)
                .fetch();

        // 쿼리 실행 (isLiked 가져오기)
        boolean isLiked=queryFactory
                .select(commentLike)
                .from(commentLike)
                .where(commentLike.cno.eq(cno), commentLike.uid.eq(myUid))
                .fetchCount() > 0;

        // DTO 구성
        CommentWithStatusDTO result=null;

        if(!tupleList.isEmpty()){
            Tuple tuple= tupleList.get(0);

            Comment comment1=tuple.get(comment);
            UserInfo userInfo1=tuple.get(userInfo);
            Long likeCount=tuple.get(commentLike.countDistinct());

            CommentDTO commentDTO= CommentService.entityToDTO(comment1, userInfo1);
            result=CommentWithStatusDTO.commentWithStatusDTOBuilder()
                    .commentDTO(commentDTO)
                    .likeCount(likeCount)
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(isLiked)
                    .build();
        }

        return Optional.ofNullable(result);
    }

    @Override
    public PageResponseDTO<CommentWithStatusDTO> search(PageRequestDTO pageRequestDTO, Long pno, String myUid){
        int pageSize=pageRequestDTO.getSize();
        Long cursor= pageRequestDTO.getCursor();

        // 쿼리 설정 (Comment, UserInfo, StorageFile, likeCount)
        JPQLQuery<Tuple> query=queryFactory
                .select(comment, userInfo, commentLike.countDistinct())
                .from(comment)
                .where(comment.post.pno.eq(pno))
                .leftJoin(userInfo).on(comment.writer.eq(userInfo))
                .leftJoin(storageFile).on(userInfo.profileImage.eq(storageFile))
                .leftJoin(commentLike).on(comment.cno.eq(commentLike.cno))
                .groupBy(comment);

        // 페이징 설정 (등록순)
        if(cursor!=null){
            query.where(comment.cno.gt(cursor));
        }

        // hasNext 를 확인하기 위해 limit (size + 1)
        query.orderBy(comment.cno.asc()).limit(pageSize+1);

        // 쿼리 실행
        List<Tuple> tupleList=query.fetch();


        // 가져온 댓글의 ID 리스트
        List<Long> cnoList=tupleList.stream().map((tuple)->tuple.get(comment).getCno()).collect(Collectors.toList());


        // 쿼리 실행 (isLiked 가져오기)
        List<Long> isLikedList= queryFactory
                .select(commentLike.cno)
                .from(commentLike)
                .where(commentLike.uid.eq(myUid), commentLike.cno.in(cnoList))
                .fetch();

        // DTO 구성
        List<CommentWithStatusDTO> dtoList=tupleList.stream().map((tuple)->{
            Comment comment1=tuple.get(comment);
            UserInfo userInfo1=tuple.get(userInfo);
            Long likeCount=tuple.get(commentLike.countDistinct());

            CommentDTO commentDTO=CommentService.entityToDTO(comment1, userInfo1);
            CommentWithStatusDTO commentWithStatusDTO=CommentWithStatusDTO.commentWithStatusDTOBuilder()
                    .commentDTO(commentDTO)
                    .likeCount(likeCount)
                    .isOwned(userInfo1.getUid().equals(myUid))
                    .isLiked(isLikedList.contains(comment1.getCno()))
                    .build();

            return commentWithStatusDTO;
        }).collect(Collectors.toList());

        // 다음 페이지가 있는지 확인
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
}
