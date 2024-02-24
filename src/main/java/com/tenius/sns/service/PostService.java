package com.tenius.sns.service;

import com.tenius.sns.domain.Post;
import com.tenius.sns.domain.StorageFile;
import com.tenius.sns.domain.UserInfo;
import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public interface PostService {
    static PostDTO entityToDTO(Post post, UserInfo userInfo){
        UserInfoDTO userInfoDTO=UserInfoService.entityToDTO(userInfo);
        PostDTO postDTO=PostDTO.builder()
                .pno(post.getPno())
                .content(post.getContent())
                .views(post.getViews())
                .regDate(post.getRegDate())
                .modDate(post.getModDate())
                .writer(userInfoDTO)
                .build();

        Set<StorageFile> files=post.getFiles();
        if(files!=null){
            List<String> fileNames=files.stream().sorted()
                    .map(file->FileService.getFileName(file.getUuid(), file.getFileName()))
                    .collect(Collectors.toList());
            postDTO.setFileNames(fileNames);
        }

        return postDTO;
    }
    static PostDTO entityToDTO(Post post){
        return entityToDTO(post, post.getWriter());
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