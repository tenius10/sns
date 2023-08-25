package com.tenius.sns.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PostWithCountDTO extends PostDTO {
    @Min(0)
    private Long commentCount;
    @Min(0)
    private Long likeCount;

    public PostWithCountDTO(PostWithCountDTO copy){
        super(copy);
        this.commentCount=copy.commentCount;
        this.likeCount=copy.likeCount;
    }
}
