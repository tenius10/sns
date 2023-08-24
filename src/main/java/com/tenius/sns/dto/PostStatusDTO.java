package com.tenius.sns.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class PostStatusDTO {
    @NotNull
    private Long pno;
    @NotNull
    private String uid;
    private boolean liked;
    private boolean hided;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
