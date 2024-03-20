package com.tenius.sns.domain;

import com.tenius.sns.service.UserInfoService;
import com.tenius.sns.util.Util;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="user_info", uniqueConstraints = {
        @UniqueConstraint(columnNames = "nickname")
})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo extends BaseEntity {
    @Id
    @Column(length= Util.UID_LENGTH, nullable = false)
    private String uid;
    @Column(length= UserInfoService.MAX_NICKNAME_LENGTH, nullable = false)
    private String nickname;
    @Builder.Default
    @Column(length=UserInfoService.MAX_INTRO_LENGTH)
    private String intro = "";
    @OneToOne(cascade = {CascadeType.ALL})
    private StorageFile profileImage;


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
