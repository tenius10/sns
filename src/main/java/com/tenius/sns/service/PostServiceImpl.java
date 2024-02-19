package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.PostStatusRepository;
import com.tenius.sns.repository.PostRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("{com.tenius.sns.upload.path}")
    private String uploadPath;
    private final PostRepository postRepository;
    private final PostStatusRepository postStatusRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final FileService fileService;
    private final ModelMapper modelMapper;

    @Override
    public PostDTO register(PostDTO postDTO, String uid) throws InputValueException {
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        Post post=Post.builder()
                .pno(null)
                .content(postDTO.getContent())
                .views(0)
                .writer(userInfo)
                .build();
        List<String> fileNames=postDTO.getFileNames();
        if(fileNames!=null){
            for(String fileName: fileNames){
                String[] arr=fileName.split(FileService.FILENAME_SEPARATOR);
                if(arr.length>1) post.addImage(arr[0], arr[1]);
                else throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
            }
        }
        Post result=postRepository.save(post);
        return entityToDTO(result);
    }

    @Override
    public PostDTO readOne(Long pno) {
        Post result=postRepository.findByIdWithImages(pno).orElseThrow();
        return entityToDTO(result);
    }

    @Override
    public PostCommentPageDTO view(Long pno, String uid){
        Post post=postRepository.findById(pno).orElseThrow();
        postRepository.save(new Post(post, post.getViews()+1));

        PostWithStatusDTO postCommentCountDTO=postRepository.findByIdWithAll(pno, uid).orElseThrow();
        PageResponseDTO<CommentWithStatusDTO> commentPage=commentRepository.search(pno, PageRequestDTO.builder().build());
        return new PostCommentPageDTO(postCommentCountDTO, commentPage);
    }

    @Override
    public PostCommentPageDTO modify(Long pno, PostDTO postDTO, String uid) throws Exception {
        Post post=postRepository.findByIdWithImages(pno).orElseThrow();

        //내용 수정
        post=new Post(post, postDTO.getContent());

        //이전에 있던 이미지 삭제
        Set<PostImage> images=post.getImages();
        List<String> beforeFileNames=post.getImages().stream()
                .map(image->FileService.getFileName(image.getUuid(), image.getFileName()))
                .collect(Collectors.toList());
        fileService.remove(beforeFileNames);

        post.clearImages();

        //새로운 이미지 추가
        if(postDTO.getFileNames()!=null){
            for(String fileName: postDTO.getFileNames()){
                String[] arr=fileName.split("_");
                post.addImage(arr[0], arr[1]);
            }
        }
        //변경사항 저장
        postRepository.save(post);

        PostWithStatusDTO postCommentCountDTO=postRepository.findByIdWithAll(pno, uid).orElseThrow();
        PageResponseDTO<CommentWithStatusDTO> commentPage=commentRepository.search(pno, PageRequestDTO.builder().build());
        return new PostCommentPageDTO(postCommentCountDTO, commentPage);
    }

    @Override
    public void remove(Long pno) throws Exception {
        Post post=postRepository.findByIdWithImages(pno).orElseThrow();
        PostDTO postDTO=entityToDTO(post);

        //게시글 삭제 (DB 상에서, 게시글 삭제)
        postRepository.deleteById(pno);

        //첨부파일 삭제 (스토리지 상에서, 파일 삭제)
        fileService.remove(postDTO.getFileNames());
    }

    @Override
    public PageResponseDTO<PostWithStatusDTO> readPage(PageRequestDTO pageRequestDTO, String uid) {
        Pageable pageable=pageRequestDTO.getPageable();
        LocalDateTime cursor=pageRequestDTO.getCursor()!=null?pageRequestDTO.getCursor().getRegDate() : null;
        PageResponseDTO<PostWithStatusDTO> result=postRepository.search(pageable, cursor, uid);
        return result;
    }

    @Override
    public boolean isPostWriter(Long pno, String uid){
        Post post=postRepository.findById(pno).orElseThrow();
        return post.getWriter().getUid().equals(uid);
    }

    @Override
    public PostWithStatusDTO like(Long pno, String uid){
        PostStatusKey key=PostStatusKey.builder().pno(pno).uid(uid).build();
        Optional<PostStatus> optional=postStatusRepository.findById(key);

        //좋아요가 이미 눌린 상황이 아니라면
        if(!(!optional.isEmpty()&&optional.get().isLiked())){
            PostStatus postStatus=optional.isEmpty()?
                    PostStatus.builder().pno(pno).uid(uid).build()
                    : optional.get();
            postStatus=new PostStatus(postStatus, true, postStatus.isHided());
            postStatusRepository.saveWithCheck(postStatus);
        }

        PostWithStatusDTO result=postRepository.findByIdWithAll(pno, uid).orElseThrow();
        return result;
    }

    @Override
    public PostWithStatusDTO unlike(Long pno, String uid){
        PostStatusKey key=PostStatusKey.builder().pno(pno).uid(uid).build();
        Optional<PostStatus> optional=postStatusRepository.findById(key);

        //좋아요가 이미 취소된 상황이 아니라면
        if(!optional.isEmpty() && optional.get().isLiked()){
            PostStatus postStatus=optional.get();
            postStatus=new PostStatus(postStatus, false, postStatus.isHided());
            postStatusRepository.saveWithCheck(postStatus);
        }

        PostWithStatusDTO result=postRepository.findByIdWithAll(pno, uid).orElseThrow();
        return result;
    }
}