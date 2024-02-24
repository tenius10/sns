package com.tenius.sns.controller;

import com.tenius.sns.dto.UploadRequestDTO;
import com.tenius.sns.dto.UploadResponseDTO;
import com.tenius.sns.exception.TokenException;
import com.tenius.sns.security.UserDetailsImpl;
import com.tenius.sns.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;

    @ApiOperation("파일 업로드")
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value="", consumes= MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List> upload(UploadRequestDTO uploadRequestDTO){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        if(userDetails==null){
            throw new TokenException(TokenException.TOKEN_ERROR.UNACCEPT);
        }

        List<MultipartFile> files=uploadRequestDTO.getFiles();
        try{
            List<UploadResponseDTO> result=fileService.upload(files);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }catch(IOException e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @ApiOperation("파일 조회")
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> view(@PathVariable String fileName){
        // '/api/files/default-profile' 로 호출하면 기본 프로필 전송
        if(fileName.equals("default-profile")) fileName=FileService.DEFAULT_PROFILE;
        Resource resource= fileService.view(fileName);
        HttpHeaders headers=new HttpHeaders();
        try{
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
    }

    @ApiOperation("썸네일 이미지 조회")
    @GetMapping("/thumbnail/{fileName}")
    public ResponseEntity<Resource> viewThumbnail(@PathVariable String fileName){
        Resource resource= fileService.view(FileService.getThumbFileName(fileName));
        HttpHeaders headers=new HttpHeaders();
        try{
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(resource);
    }

    @ApiOperation("파일 삭제")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> remove(@PathVariable String fileName){
        try{
            fileService.remove(fileName);
        } catch(Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
