package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentStatusDTO {
    @NotNull
    private Long cno;
    @NotNull
    private String uid;
    private boolean liked;
    private boolean hided;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
