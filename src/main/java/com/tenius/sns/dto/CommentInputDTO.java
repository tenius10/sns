package com.tenius.sns.dto;

import com.tenius.sns.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentInputDTO {
    @NotBlank
    @Size(max= CommentService.MAX_CONTENT_LENGTH)
    private String content;
}
