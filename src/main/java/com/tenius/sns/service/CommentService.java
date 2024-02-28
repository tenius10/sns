package com.tenius.sns.service;

import com.tenius.sns.domain.Comment;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;

public interface CommentService {
    static CommentDTO entityToDTO(Comment comment, UserInfo userInfo){
        if(comment==null) return null;
        Long pno=null;
        if(comment.getPost()!=null) pno=comment.getPost().getPno();
        UserInfoDTO userInfoDTO=UserInfoService.entityToDTO(userInfo);

        CommentDTO commentDTO=CommentDTO.builder()
                .cno(comment.getCno())
                .content(comment.getContent())
                .writer(userInfoDTO)
                .pno(pno)
                .regDate(comment.getRegDate())
                .modDate(comment.getModDate())
                .build();

        return commentDTO;
    }
    static CommentDTO entityToDTO(Comment comment){
        return entityToDTO(comment, comment.getWriter());
    }

    CommentDTO register(CommentDTO commentDTO, Long pno, String uid);
    CommentDTO readOne(Long cno);
    CommentDTO modify(Long cno, CommentDTO commentDTO);
    void remove(Long cno);
    PageResponseDTO<CommentWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, Long pno, String uid);
    boolean isCommentWriter(Long cno, String uid);
    CommentWithStatusDTO like(Long cno, String uid);
    CommentWithStatusDTO unlike(Long cno, String uid);
}
