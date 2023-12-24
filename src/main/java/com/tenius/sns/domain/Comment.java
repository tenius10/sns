package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name="comment",indexes={
        @Index(name="idx_comment_post_pno", columnList="post_pno"),
        @Index(name="idx_comment_writer_uid", columnList = "writer_uid")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude= {"post", "writer"})
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long cno;
    private String content;
    @ManyToOne(fetch= FetchType.LAZY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private UserInfo writer;
    @ManyToOne(fetch= FetchType.LAZY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private Post post;

    public Comment(Comment copy){
        super(copy.getRegDate(), copy.getModDate());
        this.cno=copy.getCno();
        this.content=copy.getContent();
        this.writer=copy.getWriter();
        this.post=copy.getPost();
    }
    public Comment(Comment copy, String content){
        this(copy);
        this.content=content;
    }
}
