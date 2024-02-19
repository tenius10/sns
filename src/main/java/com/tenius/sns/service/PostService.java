package com.tenius.sns.service;

import com.tenius.sns.domain.Post;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;

import java.util.List;
import java.util.stream.Collectors;


public interface PostService {
    default PostDTO entityToDTO(Post post){
        UserInfo userInfo=post.getWriter();
        UserInfoDTO userInfoDTO= UserInfoDTO.builder()
                .uid(userInfo.getUid())
                .nickname(userInfo.getNickname())
                .build();
        PostDTO postDTO=PostDTO.builder()
                .pno(post.getPno())
                .content(post.getContent())
                .views(post.getViews())
                .regDate(post.getRegDate())
                .modDate(post.getModDate())
                .writer(userInfoDTO)
                .build();
        List<String> fileNames=post.getImages().stream().sorted()
                .map(image->FileService.getFileName(image.getUuid(), image.getFileName()))
                .collect(Collectors.toList());
        postDTO.setFileNames(fileNames);
        return postDTO;
    }
    PostDTO register (PostDTO postDTO, String uid) throws InputValueException;
    PostDTO readOne(Long pno);
    PostCommentPageDTO view(Long pno, String uid);
    PostCommentPageDTO modify(Long pno, PostDTO postDTO, String uid) throws Exception ;
    void remove(Long pno) throws Exception;
    PageResponseDTO<PostWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, String uid);
    boolean isPostWriter(Long pno, String uid);
    PostWithStatusDTO like(Long pno, String uid);
    PostWithStatusDTO unlike(Long pno, String uid);
}