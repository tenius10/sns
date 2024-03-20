package com.tenius.sns.domain;

import com.tenius.sns.service.PostService;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes={
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
    @Column(length= PostService.MAX_CONTENT_LENGTH, nullable=false)
    private String content;
    @Builder.Default
    private int views = 0;
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private UserInfo writer;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private Set<StorageFile> files=new HashSet<>();


    public void changeContent(String content){
        this.content=content;
    }
    public void changeViews(int views){
        this.views=views;
    }

    public void addFile(String uuid, String fileName){
        StorageFile file= StorageFile.builder()
                .uuid(uuid)
                .fileName(fileName)
                .ord(files.size())
                .build();
        files.add(file);
    }

    public void clearFiles(){
        this.files.clear();
    }
}
