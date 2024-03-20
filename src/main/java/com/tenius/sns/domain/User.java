package com.tenius.sns.domain;

import com.tenius.sns.service.AuthService;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="user_auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @Column(length= AuthService.MAX_USERNAME_LENGTH, nullable=false)
    private String username;
    @Column(nullable=false)
    private String password;
    @Column(length=AuthService.MAX_EMAIL_LENGTH)
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    private UserInfo userInfo;
}
