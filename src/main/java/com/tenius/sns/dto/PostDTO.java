package com.tenius.sns.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

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
    private int views;

    public PostDTO(PostDTO copy){
        this.pno=copy.pno;
        this.content=copy.content;
        this.writer=copy.writer;
        this.regDate=copy.regDate;
        this.modDate=copy.modDate;
        this.views=copy.views;
    }

    @Override
    public LocalDateTime getRegDate(){
        return regDate;
    }
}
