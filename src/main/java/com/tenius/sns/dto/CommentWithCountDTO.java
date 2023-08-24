package com.tenius.sns.dto;

import lombok.*;

import javax.validation.constraints.Min;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CommentWithCountDTO extends CommentDTO {
    @Min(0)
    private Long likeCount;

    public CommentWithCountDTO(CommentWithCountDTO copy){
        super(copy);
        this.likeCount=copy.likeCount;
    }
}
