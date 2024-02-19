package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="post",indexes={
        @Index(name="idx_post_writer_uid", columnList = "writer_uid")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude= {"writer", "images"})
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long pno;
    @Column(length=300, nullable=false)
    private String content;
    @Builder.Default
    private int views=0;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private UserInfo writer;

    @OneToMany(mappedBy = "post",
            cascade = {CascadeType.ALL},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @Builder.Default
    @BatchSize(size=20)
    private Set<PostImage> images=new HashSet<>();

    public Post(Post copy){
        super(copy.getRegDate(), copy.getModDate());
        this.pno=copy.pno;
        this.content=copy.content;
        this.views=copy.views;
        this.writer=copy.writer;
        this.images=copy.images;
    }
    public Post(Post copy, String content){
        this(copy);
        this.content=content;
    }
    public Post(Post copy, int views){
        this(copy);
        this.views=views;
    }

    public void addImage(String uuid, String fileName){
        PostImage image=PostImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .ord(images.size())
                .post(this)
                .build();
        images.add(image);
    }

    public void clearImages(){
        images.forEach(image->image.changePost(null));
        this.images.clear();
    }
}
