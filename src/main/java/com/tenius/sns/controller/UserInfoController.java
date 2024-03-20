package com.tenius.sns.controller;

import com.tenius.sns.dto.*;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.FollowService;
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
    private final FollowService followService;
    private final PostService postService;


    /**
     * 나의 유저 정보 조회
     * @return 나의 유저 정보 (닉네임, 프로필, 자기소개 등)
     */
    @ApiOperation("나의 유저 정보 조회")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> readMe(){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal!=null && principal instanceof UserDetailsImpl){
            String uid=((UserDetailsImpl)principal).getUid();
            UserInfoDTO result=userInfoService.read(uid);
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        else{
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
    }

    /**
     * 유저 페이지 조회
     * @param uid 유저 ID
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

    @ApiOperation("팔로우")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{followeeUid}/follow")
    public ResponseEntity<Void> follow(@PathVariable String followeeUid){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal!=null && principal instanceof UserDetailsImpl){
            String myUid=((UserDetailsImpl)principal).getUid();
            followService.follow(myUid, followeeUid);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else{
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
    }

    @ApiOperation("언팔로우")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{followeeUid}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable String followeeUid){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal!=null && principal instanceof UserDetailsImpl){
            String myUid=((UserDetailsImpl)principal).getUid();
            followService.unfollow(myUid, followeeUid);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        else{
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }
    }

    @ApiOperation("팔로워 목록 조회")
    @GetMapping("/{uid}/followers")
    public ResponseEntity<PageResponseDTO> listFollower(@PathVariable String uid, String cursor){
        //uid 추출
        String myUid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        //페이지 조회
        PageResponseDTO<FollowDTO> result=followService.readFollowerPage(cursor, uid, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @ApiOperation("팔로잉 목록 조회")
    @GetMapping("/{uid}/followings")
    public ResponseEntity<PageResponseDTO> listFollowing(@PathVariable String uid, String cursor){
        //uid 추출
        String myUid="";
        Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetailsImpl){
            myUid=((UserDetailsImpl)principal).getUid();
        }

        //페이지 조회
        PageResponseDTO<FollowDTO> result=followService.readFollowingPage(cursor, uid, myUid);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 유저와 관련된 게시글 (본인이 작성한 글, 좋아요 누른 글) 목록 조회
     * @param uid 유저 ID
     * @param pageRequestDTO
     * @return
     */
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
}
