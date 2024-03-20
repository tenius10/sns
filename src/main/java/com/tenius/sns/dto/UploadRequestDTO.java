package com.tenius.sns.dto;

import com.tenius.sns.service.FileService;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UploadRequestDTO {
    @Size(max = FileService.MAX_UPLOAD_COUNT)
    private List<MultipartFile> files;
}
