package com.tenius.sns.controller;

import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.PostService;
import com.tenius.sns.service.UserInfoService;
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
@RequestMapping("/api/users")
public class UserInfoController {
    private final UserInfoService userInfoService;
    private final PostService postService;

    @ApiOperation("유저 검색")
    @GetMapping("/")
    public ResponseEntity<PageResponseDTO> search(String cursor, PageRequestDTO pageRequestDTO){
        log.info("cursor: "+cursor);
        log.info("pageRequestDTO.curosr: "+pageRequestDTO.getCursor());
        PageResponseDTO result=null;
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 로그인 직후, 나의 기본 정보를 가져오기 위해서 사용하는 API
     * @return 나의 기본 정보 (닉네임, 프로필 등)
     */
    @ApiOperation("나의 정보 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> readMe(){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        String uid=userDetails.getUid();
        UserInfoDTO result=userInfoService.read(uid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 특정 유저 페이지를 가지고 오는 API
     * @param uid
     * @return 유저 페이지 (닉네임, 프로필, 자기소개, 글 모아보기 등)
     */
    @ApiOperation("유저 페이지 조회")
    @GetMapping("/{uid}")
    public ResponseEntity<UserPageDTO> readPage(@PathVariable String uid){
        //uid 추출
        String myUid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        UserPageDTO result=userInfoService.readPage(uid, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("유저 게시글 목록 조회")
    @GetMapping("/{uid}/posts")
    public ResponseEntity<PageResponseDTO> listPost(@PathVariable String uid, PageRequestDTO pageRequestDTO){
        //uid 추출
        String myUid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        //페이지 조회
        PageRequestDTO pageRequestDTO1= PageRequestDTO.builder()
                .cursor(pageRequestDTO.getCursor())
                .build();
        PageResponseDTO<PostWithStatusDTO> pageResponseDTO=postService.readPage(pageRequestDTO1, uid, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(pageResponseDTO);
    }

    @ApiOperation("회원 정보 수정")
    @PreAuthorize("#uid == principal.getUid()")
    @PutMapping(value="/{uid}", consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserInfoDTO> update(@PathVariable String uid, @Valid @RequestBody UserInfoDTO userInfoDTO){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails=(UserDetailsImpl)principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
        try{
            UserInfoDTO result=userInfoService.modify(uid, userInfoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch(InputValueException e){
            log.error("이미지 타입의 프로필 사진을 넣어주세요.");
            throw e;
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
