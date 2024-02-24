package com.tenius.sns.controller;

import com.tenius.sns.dto.UserInfoDTO;
import com.tenius.sns.exception.InputValueException;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
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

    /**
     * 특정 유저 페이지를 가지고 오는 API
     * @param uid
     * @return 유저 페이지 (닉네임, 프로필, 자기소개, 글 모아보기 등)
     */
    @ApiOperation("유저 페이지 조회")
    @GetMapping("/{uid}")
    public ResponseEntity<UserInfoDTO> read(@PathVariable String uid){
        UserInfoDTO result=userInfoService.read(uid);
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
