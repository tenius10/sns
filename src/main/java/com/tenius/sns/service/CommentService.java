package com.tenius.sns.service;

import com.tenius.sns.dto.CommentDTO;
import com.tenius.sns.dto.CommentWithStatusDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

public interface CommentService {
    CommentDTO register(CommentDTO commentDTO, Long pno, String uid);
    CommentWithStatusDTO readOne(Long cno);
    CommentDTO modify(Long cno, CommentDTO commentDTO);
    void remove(Long cno);
    PageResponseDTO<CommentWithStatusDTO> readPage(Long pno, PageRequestDTO pageRequestDTO);
    boolean isCommentWriter(Long cno, String uid);
    CommentWithStatusDTO like(Long cno, String uid);
    CommentWithStatusDTO unlike(Long cno, String uid);
}
