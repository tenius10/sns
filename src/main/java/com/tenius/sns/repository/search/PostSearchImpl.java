package com.tenius.sns.repository.search;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.service.PostService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class PostSearchImpl extends QuerydslRepositorySupport implements PostSearch {
    public PostSearchImpl(){
        super(Post.class);
    }

    /**
     * 게시글 목록 페이지의 정보를 가져오는 메서드
     * @param pageable
     * @param cursor
     * @param uid
     * @return 게시글 목록
     */
    @Override
    public PageResponseDTO<PostWithStatusDTO> search(Pageable pageable, LocalDateTime cursor, String uid){
        //다음 페이지 존재 여부를 알기 위해서 pageSize를 하나 더하기
        int pageSize=pageable.getPageSize();
        pageable=PageRequest.of(0, pageSize+1, pageable.getSort());

        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QPostStatus postStatus=QPostStatus.postStatus;
        QStorageFile storageFile=QStorageFile.storageFile;

        //쿼리 설정 (좋아요 정보 가져오기)
        JPQLQuery<Post> query=from(post)
                .leftJoin(userInfo).on(post.writer.eq(userInfo))
                .leftJoin(storageFile).on(userInfo.profileImage.eq(storageFile))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post.pno);
        //pivot 설정
        if(cursor!=null){
            if(pageable.getSort().getOrderFor("regDate").isDescending()){
                query.where(post.regDate.before(cursor));  //최신순
            }
            else{
                query.where(post.regDate.after(cursor));  //등록순
            }
        }
        //페이징 설정
        this.getQuerydsl().applyPagination(pageable, query);
        //쿼리 실행 (게시글, 작성자, 댓글 개수 가져오기)
        List<Tuple> tupleList=query.select(post, userInfo, comment.countDistinct()).fetch();

        //쿼리 실행 (좋아요 정보, 각 게시글에 좋아요를 눌렀는지 여부 가져오기)
        Map<Long, Long> likeCountMap=new HashMap<>();  //<pno, likeCount>
        Map<Long, Boolean> likedMap=new HashMap<>();  //<pno, liked>

        List<Long> pnoList=tupleList.stream()
                .map(tuple->tuple.get(post).getPno())
                .collect(Collectors.toList());
        if(pnoList!=null && pnoList.size()>0){
            //좋아요 개수
            List<Tuple> likeCountList=from(postStatus)
                    .select(postStatus.pno, postStatus.countDistinct())
                    .where(postStatus.pno.in(pnoList), postStatus.liked.isTrue())
                    .groupBy(postStatus.pno)
                    .fetch();
            for(Tuple t: likeCountList){
                likeCountMap.put(t.get(postStatus.pno), t.get(postStatus.countDistinct()));
            }
            //좋아요 눌렀는지 여부
            List<Tuple> likedList= from(postStatus)
                    .select(postStatus.pno, postStatus.liked)
                    .where(postStatus.uid.eq(uid), postStatus.pno.in(pnoList), postStatus.liked.isTrue())
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
                    .isOwned(userInfo1.getUid().equals(uid))
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
    public Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String uid){
        //Q 도메인
        QPost post= QPost.post;
        QUserInfo userInfo=QUserInfo.userInfo;
        QComment comment=QComment.comment;
        QPostStatus postStatus=QPostStatus.postStatus;
        QStorageFile storageFile=QStorageFile.storageFile;

        //쿼리 실행 (게시글, 작성자, 댓글 개수, 좋아요 정보 가져오기)
        List<Tuple> tupleList=from(post)
                .where(post.pno.eq(pno))
                .leftJoin(userInfo).on(userInfo.eq(post.writer))
                .leftJoin(storageFile).on(storageFile.eq(userInfo.profileImage))
                .leftJoin(comment).on(comment.post.eq(post))
                .groupBy(post)
                .select(post, userInfo, comment.countDistinct())
                .fetch();
        List<String> likeList=from(postStatus)
                .where(postStatus.pno.eq(pno), postStatus.liked.isTrue())
                .select(postStatus.uid)
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
            result.setOwned(result.getWriter().getUid().equals(uid));
            result.setLiked(likeList.contains(uid));
        }

        return Optional.ofNullable(result);
    }
}