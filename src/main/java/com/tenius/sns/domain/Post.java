package com.tenius.sns.domain;

import lombok.*;
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
@ToString(exclude= {"writer", "files"})
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

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private Set<StorageFile> files=new HashSet<>();

    public Post(Post copy){
        super(copy.getRegDate(), copy.getModDate());
        this.pno=copy.pno;
        this.content=copy.content;
        this.views=copy.views;
        this.writer=copy.writer;
        this.files=copy.files;
    }
    public Post(Post copy, String content){
        this(copy);
        this.content=content;
    }
    public Post(Post copy, int views){
        this(copy);
        this.views=views;
    }

    public void addFile(String uuid, String fileName){
        StorageFile file= StorageFile.builder()
                .uuid(uuid)
                .fileName(fileName)
                .ord(files.size())
                .uploader(this.writer)
                .build();
        files.add(file);
    }

    public void clearFiles(){
        this.files.clear();
    }
}
