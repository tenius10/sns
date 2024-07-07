package com.tenius.sns.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Long pno;
    private String content;
    private UserInfoDTO writer;
    @Builder.Default
    private int views=0;
    private List<String> fileNames;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    @JsonIgnore
    private LocalDateTime modDate;

    public PostDTO(PostDTO copy){
        this.pno=copy.pno;
        this.content=copy.content;
        this.writer=copy.writer;
        this.regDate=copy.regDate;
        this.modDate=copy.modDate;
        this.views=copy.views;
        this.fileNames=copy.fileNames;
    }
}
