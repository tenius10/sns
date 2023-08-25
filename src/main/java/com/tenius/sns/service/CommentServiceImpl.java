package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.CommentDTO;
import com.tenius.sns.dto.CommentWithCountDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.CommentStatusRepository;
import com.tenius.sns.repository.PostRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

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
        return modelMapper.map(result, CommentDTO.class);
    }

    @Override
    public CommentWithCountDTO readOne(Long cno){
        CommentWithCountDTO result=commentRepository.findByIdWithAll(cno).orElseThrow();
        return result;
    }

    @Override
    public CommentDTO modify(Long cno, CommentDTO commentDTO){
        Comment comment=commentRepository.findById(cno).orElseThrow();
        comment=new Comment(comment, commentDTO.getContent());
        Comment result=commentRepository.save(comment);
        return modelMapper.map(result, CommentDTO.class);
    }

    @Override
    public void remove(Long cno){
        commentRepository.deleteById(cno);
    }


    @Override
    public PageResponseDTO<CommentWithCountDTO> readPage(Long pno, PageRequestDTO pageRequestDTO){
        PageResponseDTO<CommentWithCountDTO> result=commentRepository.search(pno, pageRequestDTO);
        return result;
    }

    @Override
    public boolean isCommentWriter(Long cno, String uid){
        Comment comment=commentRepository.findById(cno).orElseThrow();
        return comment.getWriter().getUid().equals(uid);
    }

    @Override
    public CommentWithCountDTO like(Long cno, String uid){
        CommentStatusKey key=CommentStatusKey.builder().cno(cno).uid(uid).build();
        Optional<CommentStatus> optional=commentStatusRepository.findById(key);

        //좋아요가 이미 눌린 상황이 아니라면
        if(!(!optional.isEmpty()&&optional.get().isLiked())){
            CommentStatus commentStatus=optional.isEmpty()?
                    CommentStatus.builder().cno(cno).uid(uid).build()
                    : optional.get();
            commentStatus=new CommentStatus(commentStatus, true, commentStatus.isHided());
            commentStatusRepository.saveWithCheck(commentStatus);
        }

        CommentWithCountDTO result=commentRepository.findByIdWithAll(cno).orElseThrow();
        return result;
    }

    @Override
    public CommentWithCountDTO unlike(Long cno, String uid){
        CommentStatusKey key=CommentStatusKey.builder().cno(cno).uid(uid).build();
        Optional<CommentStatus> optional=commentStatusRepository.findById(key);

        //좋아요가 이미 취소된 상황이 아니라면
        if(!optional.isEmpty() && optional.get().isLiked()){
            CommentStatus commentStatus=optional.get();
            commentStatus=new CommentStatus(commentStatus, false, commentStatus.isHided());
            commentStatusRepository.saveWithCheck(commentStatus);
        }

        CommentWithCountDTO result=commentRepository.findByIdWithAll(cno).orElseThrow();
        return result;
    }
}
