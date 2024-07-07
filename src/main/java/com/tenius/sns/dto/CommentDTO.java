package com.tenius.sns.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tenius.sns.service.CommentService;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long cno;
    private String content;
    private Long pno;
    private UserInfoDTO writer;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    @JsonIgnore
    private LocalDateTime modDate;

    public CommentDTO(CommentDTO copy){
        this.cno=copy.cno;
        this.content=copy.content;
        this.pno=copy.pno;
        this.writer=copy.writer;
        this.regDate=copy.regDate;
        this.modDate=copy.modDate;
    }
}
