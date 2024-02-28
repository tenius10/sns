package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.CommentDTO;
import com.tenius.sns.dto.CommentWithStatusDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.CommentStatusRepository;
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
    private final CommentStatusRepository commentStatusRepository;
    private final PostRepository postRepository;
    private final UserInfoRepository userInfoRepository;

    @Override
    public CommentDTO register(CommentDTO commentDTO, Long pno, String uid){
        Post post=postRepository.findById(pno).orElseThrow();
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        Comment comment=Comment.builder()
                .content(commentDTO.getContent())
                .post(post)
                .writer(userInfo)
                .build();
        Comment result=commentRepository.save(comment);
        return CommentService.entityToDTO(result);
    }

    @Override
    public CommentDTO readOne(Long cno){
        Comment result=commentRepository.findById(cno).orElseThrow();
        return CommentService.entityToDTO(result);
    }

    @Override
    public CommentDTO modify(Long cno, CommentDTO commentDTO){
        Comment comment=commentRepository.findById(cno).orElseThrow();
        comment.changeContent(commentDTO.getContent());
        Comment result=commentRepository.save(comment);
        return CommentService.entityToDTO(result);
    }

    @Override
    public void remove(Long cno){
        commentRepository.deleteById(cno);
    }


    @Override
    public PageResponseDTO<CommentWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, Long pno, String uid){
        PageResponseDTO<CommentWithStatusDTO> result=commentRepository.search(pageRequestDTO, pno, uid);
        return result;
    }

    @Override
    public boolean isCommentWriter(Long cno, String uid){
        Comment comment=commentRepository.findById(cno).orElseThrow();
        return comment.getWriter().getUid().equals(uid);
    }

    @Override
    public CommentWithStatusDTO like(Long cno, String uid){
        CommentStatusKey key=CommentStatusKey.builder().cno(cno).uid(uid).build();
        Optional<CommentStatus> optional=commentStatusRepository.findById(key);

        //좋아요가 이미 눌린 상황이 아니라면
        if(!(!optional.isEmpty()&&optional.get().isLiked())){
            CommentStatus commentStatus=optional.isEmpty()?
                    CommentStatus.builder().cno(cno).uid(uid).build()
                    : optional.get();
            commentStatus.changeLiked(true);
            commentStatusRepository.saveWithCheck(commentStatus);
        }

        CommentWithStatusDTO result=commentRepository.findByIdWithAll(cno, uid).orElseThrow();
        return result;
    }

    @Override
    public CommentWithStatusDTO unlike(Long cno, String uid){
        CommentStatusKey key=CommentStatusKey.builder().cno(cno).uid(uid).build();
        Optional<CommentStatus> optional=commentStatusRepository.findById(key);

        //좋아요가 이미 취소된 상황이 아니라면
        if(!optional.isEmpty() && optional.get().isLiked()){
            CommentStatus commentStatus=optional.get();
            commentStatus.changeLiked(false);
            commentStatusRepository.saveWithCheck(commentStatus);
        }

        CommentWithStatusDTO result=commentRepository.findByIdWithAll(cno,uid).orElseThrow();
        return result;
    }
}
