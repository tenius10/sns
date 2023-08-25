package com.tenius.sns.service;

import com.tenius.sns.dto.CommentDTO;
import com.tenius.sns.dto.CommentWithCountDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

public interface CommentService {
    CommentDTO register(CommentDTO commentDTO, Long pno, String uid);
    CommentWithCountDTO readOne(Long cno);
    CommentDTO modify(Long cno, CommentDTO commentDTO);
    void remove(Long cno);
    PageResponseDTO<CommentWithCountDTO> readPage(Long pno, PageRequestDTO pageRequestDTO);
    boolean isCommentWriter(Long cno, String uid);
    CommentWithCountDTO like(Long cno, String uid);
    CommentWithCountDTO unlike(Long cno, String uid);
}
