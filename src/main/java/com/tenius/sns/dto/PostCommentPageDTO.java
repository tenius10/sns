package com.tenius.sns.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "commentPage")
public class PostCommentPageDTO extends PostWithStatusDTO implements CursorDTO {
    private PageResponseDTO<CommentWithStatusDTO> commentPage;

    @Builder(builderMethodName = "postCommentPageDTOBuilder")
    public PostCommentPageDTO(PostWithStatusDTO postWithStatusDTO, PageResponseDTO<CommentWithStatusDTO> commentPage){
        super(postWithStatusDTO);
        this.commentPage=commentPage;
    }
}
