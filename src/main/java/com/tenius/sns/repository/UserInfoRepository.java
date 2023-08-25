package com.tenius.sns.repository;

import com.tenius.sns.domain.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    Boolean existsByNickname(String nickname);
}
