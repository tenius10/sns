package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.CommentLikeRepository;
import com.tenius.sns.repository.PostRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public Long register(CommentInputDTO commentInputDTO, Long pno, String myUid){
        // 게시글 정보 가져오기
        Post post=postRepository.findById(pno).orElseThrow();

        // 작성자의 유저 정보 가져오기
        UserInfo userInfo=userInfoRepository.findById(myUid).orElseThrow();

        // Comment 엔티티 생성
        Comment comment=Comment.builder()
                .content(commentInputDTO.getContent())
                .post(post)
                .writer(userInfo)
                .build();

        // 댓글 엔티티 DB에 저장
        Comment result=commentRepository.save(comment);
        
        // 등록된 댓글의 ID 반환
        return result.getCno();
    }

    @Override
    public CommentDTO read(Long cno){
        // 댓글 조회
        Comment result=commentRepository.findById(cno).orElseThrow();
        return CommentService.entityToDTO(result);
    }

    @Override
    public CommentWithStatusDTO readWithStatus(Long cno, String myUid){
        CommentWithStatusDTO result=commentRepository.findByIdWithAll(cno, myUid).orElseThrow();
        return result;
    }

    @Override
    public Long modify(Long cno, CommentInputDTO commentInputDTO){
        // 기존 댓글 정보 가져오기
        Comment comment=commentRepository.findById(cno).orElseThrow();
        
        // 댓글 수정
        comment.changeContent(commentInputDTO.getContent());
        
        // 수정한 댓글을 DB에 저장
        Comment result=commentRepository.save(comment);
        
        // 수정된 댓글의 ID 반환
        return result.getCno();
    }

    @Override
    public void remove(Long cno){
        // 댓글을 DB에서 삭제
        commentRepository.deleteById(cno);
    }


    @Override
    public PageResponseDTO<CommentWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, Long pno, String myUid){
        // 페이지 조회
        PageResponseDTO<CommentWithStatusDTO> result=commentRepository.search(pageRequestDTO, pno, myUid);
        return result;
    }

    @Override
    public boolean isCommentWriter(Long cno, String myUid){
        // 댓글 정보 가져오기
        Comment comment=commentRepository.findById(cno).orElseThrow();
        
        // 댓글의 작성자와 myUid 비교
        return comment.getWriter().getUid().equals(myUid);
    }

    @Override
    public Long like(Long cno, String myUid){
        // 댓글 좋아요 정보를 DB에 저장
        CommentLike commentLike=CommentLike.builder().cno(cno).uid(myUid).build();
        commentLikeRepository.save(commentLike);

        // 좋아요 누른 댓글의 ID 반환
        return cno;
    }

    @Override
    public Long unlike(Long cno, String myUid){
        // 댓글 좋아요 정보를 DB에서 삭제
        CommentLikeKey commentLikeKey= CommentLikeKey.builder().cno(cno).uid(myUid).build();
        commentLikeRepository.deleteById(commentLikeKey);

        // 좋아요 취소한 댓글의 ID 반환
        return cno;
    }
}
