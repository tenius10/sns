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
    public ResponseEntity<PageResponseDTO> list(PageRequestDTO pageRequestDTO, SearchOptionDTO searchOptionDTO){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 페이지 조회
        PageResponseDTO<PostWithStatusDTO> pageResponseDTO=postService.readPage(pageRequestDTO, searchOptionDTO, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("게시글 조회")
    @GetMapping("/{pno}")
    public ResponseEntity<PostWithStatusDTO> read(@PathVariable Long pno){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 게시글 조회
        PostWithStatusDTO result=postService.viewWithStatus(pno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> create(@Valid @RequestBody PostInputDTO postInputDTO){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 인증 정보가 없는 경우 등록 불가 (에러 던지기)
        if(myUid.isEmpty()){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        
        // 게시글 등록
        Long result=postService.register(postInputDTO, myUid);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation("게시글 수정")
    @PreAuthorize("@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @PutMapping(value="/{pno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> update(@PathVariable Long pno, @Valid @RequestBody PostInputDTO postInputDTO){
        try{
            // 게시글 수정
            Long result=postService.modify(pno, postInputDTO);
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
            // 게시글 삭제
            postService.remove(pno);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation("게시글 좋아요")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @PostMapping("/{pno}/like")
    public ResponseEntity<Long> like(@PathVariable Long pno){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 인증 정보가 없는 경우 좋아요 불가 (에러 던지기)
        if(myUid.isEmpty()){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }

        // 게시글 좋아요
        Long result=postService.like(pno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("게시글 좋아요 취소")
    @PreAuthorize("isAuthenticated() and !@postServiceImpl.isPostWriter(#pno, principal.getUid())")
    @DeleteMapping("/{pno}/like")
    public ResponseEntity<Long> unlike(@PathVariable Long pno){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 인증 정보가 없는 경우 좋아요 취소 불가 (에러 던지기)
        if(myUid.isEmpty()){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }

        // 게시글 좋아요 취소
        Long result=postService.unlike(pno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
