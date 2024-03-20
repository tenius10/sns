package com.tenius.sns.service;

import com.tenius.sns.dto.UploadResponseDTO;
import com.tenius.sns.exception.InputValueException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    String FILENAME_SEPARATOR="_";
    String DEFAULT_PROFILE="default-profile.png";

    int MAX_UPLOAD_COUNT=10;

    static String getFileName(String uuid, String originalFileName){
        return uuid+FILENAME_SEPARATOR+originalFileName;
    }
    static String getThumbFileName(String uuid, String originalFileName){
        return "s"+FILENAME_SEPARATOR+uuid+FILENAME_SEPARATOR+originalFileName;
    }
    static String getThumbFileName(String fileName){
        return "s"+FILENAME_SEPARATOR+fileName;
    }

    List<UploadResponseDTO> upload(List<MultipartFile> files) throws IOException;
    Resource view(String fileName) throws InputValueException;
    void remove(String fileName) throws Exception;
    void remove(List<String> fileNames) throws Exception;
    boolean isImageFile(String fileName) throws IOException;
}
