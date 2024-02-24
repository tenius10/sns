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
@ToString
public class UserInfo extends BaseEntity {
    @Id
    @Column(length=16, nullable = false)
    private String uid;
    @Column(length=10)
    private String nickname;

    @JoinColumn(name="uid", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private User user;

    @OneToOne(cascade = {CascadeType.ALL},
            orphanRemoval = true)
    private StorageFile profileImage;

    public UserInfo(UserInfo copy, String nickname, StorageFile profileImage){
        this.uid=copy.uid;
        this.user=copy.user;
        this.nickname=nickname;
        this.profileImage=profileImage;
    }
}
