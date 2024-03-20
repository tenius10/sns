package com.tenius.sns.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageFile extends BaseEntity implements Comparable<StorageFile>{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long fno;
    private String uuid;
    private String fileName;
    private int ord;

    @Override
    public int compareTo(StorageFile other){
        return this.ord-other.ord;
    }
}
