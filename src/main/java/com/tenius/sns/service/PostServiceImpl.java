package com.tenius.sns.service;

import com.tenius.sns.domain.*;
import com.tenius.sns.dto.*;
import com.tenius.sns.repository.CommentRepository;
import com.tenius.sns.repository.PostStatusRepository;
import com.tenius.sns.repository.PostRepository;
import com.tenius.sns.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostStatusRepository postStatusRepository;
    private final CommentRepository commentRepository;
    private final UserInfoRepository userInfoRepository;
    private final ModelMapper modelMapper;

    @Override
    public PostDTO register(PostDTO postDTO, String uid) {
        UserInfo userInfo=userInfoRepository.findById(uid).orElseThrow();
        Post post=Post.builder()
                .content(postDTO.getContent())
                .writer(userInfo)
                .views(0)
                .build();
        Post result=postRepository.save(post);
        return modelMapper.map(result, PostDTO.class);
    }

    @Override
    public PostCommentPageDTO readOne(Long pno) {
        PostWithCountDTO postCommentCountDTO=postRepository.findByIdWithAll(pno).orElseThrow();
        PageResponseDTO<CommentWithCountDTO> commentPage=commentRepository.search(pno, PageRequestDTO.builder().build());
        return new PostCommentPageDTO(postCommentCountDTO, commentPage);
    }

    @Override
    public PostCommentPageDTO view(Long pno){
        Post post=postRepository.findById(pno).orElseThrow();
        postRepository.save(new Post(post, post.getViews()+1));

        PostWithCountDTO postCommentCountDTO=postRepository.findByIdWithAll(pno).orElseThrow();
        PageResponseDTO<CommentWithCountDTO> commentPage=commentRepository.search(pno, PageRequestDTO.builder().build());
        return new PostCommentPageDTO(postCommentCountDTO, commentPage);
    }

    @Override
    public PostDTO modify(Long pno, PostDTO postDTO) {
        Post post=postRepository.findById(pno).orElseThrow();
        post=new Post(post, postDTO.getContent());
        Post result=postRepository.save(post);
        return modelMapper.map(result, PostDTO.class);
    }

    @Override
    public void remove(Long pno) {
        postRepository.deleteById(pno);
    }

    @Override
    public PageResponseDTO<PostWithCountDTO> readPage(PageRequestDTO pageRequestDTO) {
        PageResponseDTO<PostWithCountDTO> result=postRepository.search(pageRequestDTO);
        return result;
    }

    @Override
    public boolean isPostWriter(Long pno, String uid){
        Post post=postRepository.findById(pno).orElseThrow();
        return post.getWriter().getUid().equals(uid);
    }

    @Override
    public PostDTO like(Long pno, String uid){
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

        Post result=postRepository.findById(pno).orElseThrow();
        return modelMapper.map(result, PostDTO.class);
    }

    @Override
    public PostDTO unlike(Long pno, String uid){
        PostStatusKey key=PostStatusKey.builder().pno(pno).uid(uid).build();
        Optional<PostStatus> optional=postStatusRepository.findById(key);

        //좋아요가 이미 취소된 상황이 아니라면
        if(!optional.isEmpty() && optional.get().isLiked()){
            PostStatus postStatus=optional.get();
            postStatus=new PostStatus(postStatus, false, postStatus.isHided());
            postStatusRepository.saveWithCheck(postStatus);
        }

        Post result=postRepository.findById(pno).orElseThrow();
        return modelMapper.map(result, PostDTO.class);
    }
}
