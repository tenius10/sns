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
@ToString(exclude = "uploader")
public class StorageFile extends BaseEntity implements Comparable<StorageFile>{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long fno;
    private String uuid;
    private String fileName;
    private int ord;
    @ManyToOne(fetch= FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private UserInfo uploader;

    @Override
    public int compareTo(StorageFile other){
        return this.ord-other.ord;
    }
}
