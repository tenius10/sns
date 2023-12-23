package com.tenius.sns.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommentWithStatusDTO extends CommentDTO {
    @Min(0)
    private Long likeCount;

    public CommentWithStatusDTO(CommentWithStatusDTO copy){
        super(copy);
        this.likeCount=copy.likeCount;
    }
}
