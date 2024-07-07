package com.tenius.sns.service;

import com.tenius.sns.domain.Comment;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;

public interface CommentService {
    int MAX_CONTENT_LENGTH=500;
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

    Long register(CommentInputDTO commentInputDTO, Long pno, String myUid);
    CommentDTO read(Long cno);
    CommentWithStatusDTO readWithStatus(Long cno, String myUid);
    Long modify(Long cno, CommentInputDTO commentInputDTO);
    void remove(Long cno);
    PageResponseDTO<CommentWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, Long pno, String myUid);
    boolean isCommentWriter(Long cno, String myUid);
    Long like(Long cno, String myUid);
    Long unlike(Long cno, String myUid);
}
