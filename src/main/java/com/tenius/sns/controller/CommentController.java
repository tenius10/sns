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
        if(pageRequestDTO.getNo()!=null){
            CommentDTO cursor=commentService.readOne(pageRequestDTO.getNo());
            pageRequestDTO.setCursor(cursor);
        }
        PageResponseDTO<CommentWithStatusDTO> pageResponseDTO=commentService.readPage(pno, pageRequestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("특정 댓글 조회")
    @GetMapping("/{cno}")
    public ResponseEntity<CommentWithStatusDTO> read(@PathVariable Long pno, @PathVariable Long cno){
        CommentWithStatusDTO result=commentService.readOne(cno);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 게시글 댓글 등록")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO> create(@PathVariable Long pno, @Valid @RequestBody CommentDTO commentDTO){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        CommentDTO result=commentService.register(commentDTO, pno, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @ApiOperation("특정 댓글 수정")
    @PreAuthorize("@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @PutMapping(value="/{cno}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDTO> update(@PathVariable Long pno, @PathVariable Long cno, @Valid @RequestBody CommentDTO commentDTO){
        CommentDTO result=commentService.modify(cno, commentDTO);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 댓글 삭제")
    @PreAuthorize("@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @DeleteMapping("/{cno}")
    public ResponseEntity<Void> delete(@PathVariable Long pno, @PathVariable Long cno){
        commentService.remove(cno);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ApiOperation("특정 댓글 좋아요")
    @PreAuthorize("isAuthenticated() and !@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @PostMapping("/{cno}/like")
    public ResponseEntity<CommentDTO> like(@PathVariable Long pno, @PathVariable Long cno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        CommentDTO result=commentService.like(cno, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("특정 댓글 좋아요 취소")
    @PreAuthorize("isAuthenticated() and !@commentServiceImpl.isCommentWriter(#cno, principal.getUid())")
    @DeleteMapping("/{cno}/like")
    public ResponseEntity<CommentDTO> unlike(@PathVariable Long pno, @PathVariable Long cno){
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        CommentDTO result=commentService.unlike(cno, userDetails.getUid());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
