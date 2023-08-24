package com.tenius.sns.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "commentPage")
public class PostCommentPageDTO extends PostWithCountDTO implements CursorDTO {
    private PageResponseDTO<CommentWithCountDTO> commentPage;

    public PostCommentPageDTO(PostWithCountDTO parent, PageResponseDTO<CommentWithCountDTO> commentPage){
        super(parent);
        this.commentPage=commentPage;
    }
}
