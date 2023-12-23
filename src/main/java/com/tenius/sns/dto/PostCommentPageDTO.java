package com.tenius.sns.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "commentPage")
public class PostCommentPageDTO extends PostWithStatusDTO implements CursorDTO {
    private PageResponseDTO<CommentWithStatusDTO> commentPage;

    public PostCommentPageDTO(PostWithStatusDTO parent, PageResponseDTO<CommentWithStatusDTO> commentPage){
        super(parent);
        this.commentPage=commentPage;
    }
}
