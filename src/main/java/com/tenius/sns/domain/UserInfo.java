package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name="user_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nickname")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"user"})
public class UserInfo extends BaseEntity {
    @Id
    @Column(length=16, nullable = false)
    private String uid;
    @Column(length=10, nullable = false)
    private String nickname;

    @Builder.Default
    @Column(length=100)
    private String intro = "";

    @OneToOne(cascade = {CascadeType.ALL})
    private StorageFile profileImage;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", updatable = false)
    private User user;

    public void changeNickname(String nickname){
        this.nickname=nickname;
    }
    public void changeIntro(String intro){
        this.intro=intro;
    }
    public void changeProfileImage(StorageFile profileImage){
        this.profileImage=profileImage;
    }
}
