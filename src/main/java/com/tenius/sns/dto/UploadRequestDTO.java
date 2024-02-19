package com.tenius.sns.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadRequestDTO {
    private List<MultipartFile> files;
}
