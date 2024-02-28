package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(name="user_auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userInfo")
public class User extends BaseEntity {
    @Id
    @Column(length=16, nullable = false)
    private String uid;
    @Column(length=15, nullable=false)
    private String username;
    @Column(nullable=false)
    private String password;
    @Column(length=50)
    private String email;
    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch=FetchType.LAZY)
    private UserInfo userInfo;

    public void initUserInfo(UserInfo userInfo){
        this.userInfo=userInfo;
    }
}
