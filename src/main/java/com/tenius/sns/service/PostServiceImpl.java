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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostStatusRepository postStatusRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final FileService fileService;

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
                if(arr.length>1) post.addFile(arr[0], arr[1]);
                else throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
            }
        }
        Post result=postRepository.save(post);
        return PostService.entityToDTO(result);
    }

    @Override
    public PostDTO readOne(Long pno) {
        Post result=postRepository.findByIdWithFiles(pno).orElseThrow();
        return PostService.entityToDTO(result);
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
        Post post=postRepository.findByIdWithFiles(pno).orElseThrow();

        //내용 수정
        post=new Post(post, postDTO.getContent());

        //이전에 있던 이미지 삭제
        List<String> beforeFileNames=post.getFiles().stream()
                .map(file->FileService.getFileName(file.getUuid(), file.getFileName()))
                .collect(Collectors.toList());
        fileService.remove(beforeFileNames);
        
        post.clearFiles();

        //새로운 이미지 추가
        List<String> fileNames=postDTO.getFileNames();
        if(fileNames!=null){
            for(String fileName: fileNames){
                String[] arr=fileName.split(FileService.FILENAME_SEPARATOR);
                if(arr.length>1) post.addFile(arr[0], arr[1]);
                else throw new InputValueException(InputValueException.ERROR.INVALID_FILE_NAME);
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
        Post post=postRepository.findByIdWithFiles(pno).orElseThrow();
        PostDTO postDTO=PostService.entityToDTO(post);

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