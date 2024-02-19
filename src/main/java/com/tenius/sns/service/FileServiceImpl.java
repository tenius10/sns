package com.tenius.sns.service;

import com.tenius.sns.dto.UploadResponseDTO;
import com.tenius.sns.exception.InputValueException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${com.tenius.sns.upload.path}")
    private String uploadPath;
    private final int THUMB_SIZE=100;
    /**
     * 입력받은 파일 리스트를 서버에 업로드해주는 메서드
     * @param files 업로드할 파일
     * @return 업로드한 파일의 정보 반환
     * @throws IOException 파일 업로드에 실패한 경우
     */
    public List<UploadResponseDTO> upload(List<MultipartFile> files) throws IOException {
        List<UploadResponseDTO> result=new ArrayList<>();
        if(files!=null){
            for(MultipartFile file: files){
                String originalName=file.getOriginalFilename().replace(FILENAME_SEPARATOR,"");
                String uuid= UUID.randomUUID().toString();
                Path savePath= Paths.get(uploadPath, FileService.getFileName(uuid, originalName));
                boolean isImage=false;

                //파일 저장
                file.transferTo(savePath);

                //만약 이미지 파일이라면, 썸네일 저장
                if(Files.probeContentType(savePath).startsWith("image")){
                    isImage=true;
                    File thumbFile=new File(uploadPath, FileService.getThumbFileName(uuid, originalName));
                    Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, THUMB_SIZE, THUMB_SIZE);
                }
                result.add(UploadResponseDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .isImage(isImage)
                        .build()
                );
            }
        }
        return result;
    }

    public Resource view(String fileName) throws InputValueException {
        Resource resource=new FileSystemResource(uploadPath+File.separator+fileName);
        if(!resource.exists()) throw new InputValueException(InputValueException.ERROR.NOT_FOUND_FILE);
        return resource;
    }

    public void remove(String fileName) throws Exception {
        Resource resource=new FileSystemResource(uploadPath+File.separator+fileName);
        if(resource.exists()){
            //파일 종류 확인
            String contentType=Files.probeContentType(resource.getFile().toPath());
            //파일 삭제
            resource.getFile().delete();
            //이미지 파일이면, 썸네일도 함께 삭제
            if(contentType.startsWith(("image"))){
                File thumbFile=new File(uploadPath+File.separator+FileService.getThumbFileName(fileName));
                if(thumbFile.exists()) thumbFile.delete();
            }
        }
    }
    public void remove(List<String> fileNames) throws Exception {
        if(fileNames!=null){
            for(String fileName : fileNames){
                remove(fileName);
            }
        }
    }
}
