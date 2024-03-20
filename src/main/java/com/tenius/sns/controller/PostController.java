package com.tenius.sns.controller;

import com.tenius.sns.dto.*;
import com.tenius.sns.exception.TokenException;
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
    public ResponseEntity<PageResponseDTO> list(PageRequestDTO pageRequestDTO){
        //uid 추출
        String uid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }

        //페이지 조회
        PageResponseDTO<PostWithStatusDTO> pageResponseDTO=postService.readPage(pageRequestDTO, null, uid);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("게시글 조회")
    @GetMapping("/{pno}")
    public ResponseEntity<PostWithStatusDTO> read(@PathVariable Long pno){
        //uid 추출
        String uid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }
        //게시글 조회
        PostWithStatusDTO result=postService.view(pno, uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDTO> create(@Valid @RequestBody PostInputDTO postInputDTO){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid=null;
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }
        if(uid==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        PostDTO result=postService.register(postInputDTO, uid);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation("게시글 수정")
    @PreAuthorize("@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @PutMapping(value="/{pno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostWithStatusDTO> update(@PathVariable Long pno, @Valid @RequestBody PostInputDTO postInputDTO){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid=null;
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }
        if(uid==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        try{
            PostWithStatusDTO result=postService.modify(pno, postInputDTO, uid);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation("게시글 삭제")
    @PreAuthorize("@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @DeleteMapping("/{pno}")
    public ResponseEntity<Void> delete(@PathVariable Long pno){
        try{
            postService.remove(pno);
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("게시글 좋아요")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @PostMapping("/{pno}/like")
    public ResponseEntity<PostWithStatusDTO> like(@PathVariable Long pno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid=null;
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }
        if(uid==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        PostWithStatusDTO result=postService.like(pno, uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 좋아요 취소")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @DeleteMapping("/{pno}/like")
    public ResponseEntity<PostWithStatusDTO> unlike(@PathVariable Long pno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String uid=null;
        if(principal instanceof UserDetailsImpl){
            uid=((UserDetailsImpl)principal).getUid();
        }
        if(uid==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        PostWithStatusDTO result=postService.unlike(pno, uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
