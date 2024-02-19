package com.tenius.sns.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude="post")
public class PostImage implements Comparable<PostImage>{
    @Id
    private String uuid;
    private String fileName;
    private int ord;
    @ManyToOne
    private Post post;

    @Override
    public int compareTo(PostImage other){
        return this.ord-other.ord;
    }

    public void changePost(Post post){
        this.post=post;
    }
}
