package com.tenius.sns.repository;

import com.tenius.sns.domain.Follow;
import com.tenius.sns.repository.search.FollowSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long>, FollowSearch {
    Boolean existsByFollowerUidAndFolloweeUid(String followerUid, String followeeUid);
    void deleteByFollowerUidAndFolloweeUid(String followerUid, String followeeUid);
    long countByFollowerUid(String followerUid);
    long countByFolloweeUid(String followeeUid);
    Optional<Follow> findByFollowerUidAndFolloweeUid(String followerUid, String followeeUid);
}
