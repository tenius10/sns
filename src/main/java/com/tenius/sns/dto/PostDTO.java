package com.tenius.sns.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO implements CursorDTO {
    private Long pno;
    @NotBlank
    @Size(max=300)
    private String content;
    private UserInfoDTO writer;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    @Min(0)
    private int views=0;
    private List<String> fileNames;

    public PostDTO(PostDTO copy){
        this.pno=copy.pno;
        this.content=copy.content;
        this.writer=copy.writer;
        this.regDate=copy.regDate;
        this.modDate=copy.modDate;
        this.views=copy.views;
        this.fileNames=copy.fileNames;
    }

    @Override
    public LocalDateTime getRegDate(){
        return regDate;
    }
}
