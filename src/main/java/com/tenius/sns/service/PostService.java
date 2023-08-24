package com.tenius.sns.service;

import com.tenius.sns.dto.*;


public interface PostService {
    PostDTO register (PostDTO postDTO, String uid);
    PostCommentPageDTO readOne(Long pno);
    PostCommentPageDTO view(Long pno);
    PostDTO modify(Long pno, PostDTO postDTO);
    void remove(Long pno);
    PageResponseDTO<PostWithCountDTO> readPage(PageRequestDTO pageRequestDTO);
    boolean isPostWriter(Long pno, String uid);
    PostDTO like(Long pno, String uid);
    PostDTO unlike(Long pno, String uid);
}
