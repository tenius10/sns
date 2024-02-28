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
    private boolean isOwned;
    private boolean isLiked;

    @Builder(builderMethodName="commentWithStatusDTOBuilder")
    public CommentWithStatusDTO(CommentDTO commentDTO, Long likeCount, boolean isOwned, boolean isLiked){
        super(commentDTO);
        this.likeCount=likeCount;
        this.isOwned=isOwned;
        this.isLiked=isLiked;
    }
}
