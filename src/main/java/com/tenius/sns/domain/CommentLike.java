package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "comment_like", indexes={
        @Index(name="idx_comment_like_cno_uid", columnList = "cno, uid")
})
@IdClass(CommentLikeKey.class)
public class CommentLike extends BaseEntity {
    @Id
    private Long cno;
    @Id
    private String uid;
}
