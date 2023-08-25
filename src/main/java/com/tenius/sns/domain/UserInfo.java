package com.tenius.sns.domain;

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
@ToString
public class UserInfo extends BaseEntity {
    @Id
    @Column(length=16, nullable = false)
    private String uid;
    @Column(length=10)
    private String nickname;
}
