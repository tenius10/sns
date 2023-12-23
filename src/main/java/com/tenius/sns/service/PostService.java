package com.tenius.sns.service;

import com.tenius.sns.dto.*;


public interface PostService {
    PostDTO register (PostDTO postDTO, String uid);
    PostDTO readOne(Long pno);
    PostCommentPageDTO view(Long pno, String uid);
    PostCommentPageDTO modify(Long pno, PostDTO postDTO, String uid);
    void remove(Long pno);
    PageResponseDTO<PostWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, String uid);
    boolean isPostWriter(Long pno, String uid);
    PostWithStatusDTO like(Long pno, String uid);
    PostWithStatusDTO unlike(Long pno, String uid);
}
