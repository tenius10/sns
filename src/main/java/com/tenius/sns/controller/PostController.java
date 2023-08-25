package com.tenius.sns.controller;

import com.tenius.sns.dto.*;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.PostService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @ApiOperation("게시글 목록 조회")
    @GetMapping("")
    public ResponseEntity<PageResponseDTO> list(@Valid PageRequestDTO pageRequestDTO){
        PageResponseDTO<PostWithCountDTO> pageResponseDTO=postService.readPage(pageRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("게시글 조회")
    @GetMapping("/{pno}")
    public ResponseEntity<PostCommentPageDTO> read(@PathVariable Long pno){
        PostCommentPageDTO result=postService.view(pno);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostDTO postDTO){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        PostDTO result=postService.register(postDTO, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation("게시글 수정")
    @PreAuthorize("@postServiceImpl.isPostWriter(#pno, @userDetailsServiceImpl.getUidFromPrincipal(principal))")
    @PutMapping(value="/{pno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> update(@PathVariable Long pno, @Valid @RequestBody PostDTO postDTO){
        PostDTO result=postService.modify(pno, postDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 삭제")
    @PreAuthorize("@postServiceImpl.isPostWriter(#pno, @userDetailsServiceImpl.getUidFromPrincipal(principal))")
    @DeleteMapping("/{pno}")
    public ResponseEntity<Void> delete(@PathVariable Long pno){
        postService.remove(pno);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("게시글 좋아요")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, @userDetailsServiceImpl.getUidFromPrincipal(principal))")
    @PostMapping("/{pno}/like")
    public ResponseEntity<PostDTO> like(@PathVariable Long pno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        PostDTO result=postService.like(pno, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 좋아요 취소")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, @userDetailsServiceImpl.getUidFromPrincipal(principal))")
    @DeleteMapping("/{pno}/like")
    public ResponseEntity<PostDTO> unlike(@PathVariable Long pno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        PostDTO result=postService.unlike(pno, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
