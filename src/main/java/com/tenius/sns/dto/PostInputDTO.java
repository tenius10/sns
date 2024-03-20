package com.tenius.sns.dto;

import com.tenius.sns.service.PostService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostInputDTO {
    @NotBlank
    @Size(max = PostService.MAX_CONTENT_LENGTH)
    private String content;
    @Size(max = PostService.MAX_FILE_COUNT)
    private List<String> fileNames;
}
