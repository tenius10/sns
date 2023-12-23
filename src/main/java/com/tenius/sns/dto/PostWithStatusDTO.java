package com.tenius.sns.dto;

import lombok.*;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PostWithStatusDTO extends PostDTO {
    @Min(0)
    private Long commentCount;
    @Min(0)
    private Long likeCount;
    private boolean isOwned;
    private boolean isLiked;

    public PostWithStatusDTO(PostWithStatusDTO copy){
        super(copy);
        this.commentCount=copy.commentCount;
        this.likeCount=copy.likeCount;
        this.isOwned=copy.isOwned;
        this.isLiked=copy.isLiked;
    }
}
