package com.tenius.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes={
        @Index(name="follow_index", columnList="follower_uid, followee_uid")
})
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fno;
    @Column(name="follower_uid")
    private String followerUid;
    @Column(name="followee_uid")
    private String followeeUid;
}
