package com.tenius.sns.controller;

import com.tenius.sns.dto.*;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.CommentService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{pno}/comments")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation("특정 게시글 댓글 목록 조회")
    @GetMapping("")
    public ResponseEntity<PageResponseDTO> list(@PathVariable Long pno, PageRequestDTO pageRequestDTO){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 페이지 조회
        PageResponseDTO<CommentWithStatusDTO> pageResponseDTO=commentService.readPage(pageRequestDTO, pno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("특정 댓글 조회")
    @GetMapping("/{cno}")
    public ResponseEntity<CommentDTO> read(@PathVariable Long pno, @PathVariable Long cno){
        // Context 에서 principal 가져오기
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 댓글 조회
        CommentDTO result=commentService.readWithStatus(cno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 게시글 댓글 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> create(@PathVariable Long pno, @Valid @RequestBody CommentInputDTO commentInputDTO){
        // Context 에서 principal 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // myUid 추출
        String myUid="";
        if(principal!=null && principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        // 인증 정보가 없는 경우 등록 불가 (에러 던지기)
        if(myUid.isEmpty()){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        
        // 댓글 등록
        Long result=commentService.register(commentInputDTO, pno, myUid);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation("특정 댓글 수정")
    @PreAuthorize("@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @PutMapping(value="/{cno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> update(@PathVariable Long pno, @PathVariable Long cno, @Valid @RequestBody CommentInputDTO commentInputDTO){
        // 댓글 수정
        Long result=commentService.modify(cno, commentInputDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 댓글 삭제")
    @PreAuthorize("@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @DeleteMapping("/{cno}")
    public ResponseEntity<Void> delete(@PathVariable Long pno, @PathVariable Long cno){
        // 댓글 삭제
        commentService.remove(cno);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("특정 댓글 좋아요")
    @PreAuthorize("isAuthenticated() and !@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @PostMapping("/{cno}/like")
    public ResponseEntity<Long> like(@PathVariable Long pno, @PathVariable Long cno){
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

        // 댓글 좋아요
        Long result=commentService.like(cno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 댓글 좋아요 취소")
    @PreAuthorize("isAuthenticated() and !@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @DeleteMapping("/{cno}/like")
    public ResponseEntity<Long> unlike(@PathVariable Long pno, @PathVariable Long cno){
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

        // 댓글 좋아요 취소
        Long result=commentService.unlike(cno, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
