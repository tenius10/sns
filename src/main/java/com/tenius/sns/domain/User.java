package com.tenius.sns.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="user_auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")})
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
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
}
