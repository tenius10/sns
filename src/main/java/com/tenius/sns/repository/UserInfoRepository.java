package com.tenius.sns.repository;

import com.tenius.sns.domain.UserInfo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    Boolean existsByNickname(String nickname);

    @EntityGraph(attributePaths={"profileImage"})
    @Query("select u from UserInfo u where u.uid=:uid")
    Optional<UserInfo> findByIdWithProfileImage(@Param("uid") String uid);
}
