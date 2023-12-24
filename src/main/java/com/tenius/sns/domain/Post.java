package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name="post",indexes={
        @Index(name="idx_post_writer_uid", columnList = "writer_uid")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude= "writer")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long pno;
    @Column(length=300, nullable=false)
    private String content;
    private int views;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private UserInfo writer;

    public Post(Post copy){
        super(copy.getRegDate(), copy.getModDate());
        this.pno=copy.getPno();
        this.content=copy.getContent();
        this.views=copy.getViews();
        this.writer=copy.getWriter();
    }
    public Post(Post copy, String content){
        this(copy);
        this.content=content;
    }
    public Post(Post copy, int views){
        this(copy);
        this.views=views;
    }
}
