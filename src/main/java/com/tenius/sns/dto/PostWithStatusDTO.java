package com.tenius.sns.dto;

import lombok.*;

import javax.validation.constraints.*;

@Setter
@Getter
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

    @Builder(builderMethodName = "postWithStatusDTOBuilder")
    public PostWithStatusDTO(PostDTO postDTO, Long commentCount, Long likeCount, boolean isOwned, boolean isLiked) {
        super(postDTO);
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.isOwned = isOwned;
        this.isLiked = isLiked;
    }
}
