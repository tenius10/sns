package com.tenius.sns.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "post_like", indexes={
        @Index(name="idx_post_like_pno_uid", columnList = "pno, uid")
})
@IdClass(PostLikeKey.class)
public class PostLike extends BaseEntity {
    @Id
    private Long pno;
    @Id
    private String uid;
}