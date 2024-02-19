package com.tenius.sns.dto;

import com.tenius.sns.service.FileService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResponseDTO {
    private String uuid;
    private String fileName;
    private boolean isImage;
    public String getLink(){
        return FileService.getFileName(uuid, fileName);
    }
}
