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
    int MAX_CONTENT_LENGTH=1000;
    int MAX_FILE_COUNT=10;

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

    Long register (PostInputDTO postInputDTO, String myUid) throws InputValueException;
    PostDTO readWithFiles(Long pno);
    PostWithStatusDTO viewWithStatus(Long pno, String myUid);
    Long modify(Long pno, PostInputDTO postInputDTO) throws Exception ;
    void remove(Long pno) throws Exception;
    PageResponseDTO<PostWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, SearchOptionDTO searchOptionDTO, String myUid);
    boolean isPostWriter(Long pno, String myUid);
    Long like(Long pno, String myUid);
    Long unlike(Long pno, String myUid);
}