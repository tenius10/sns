package com.tenius.sns.repository;

import com.tenius.sns.domain.PostLikeKey;
import com.tenius.sns.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeKey> {
}
