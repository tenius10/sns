package com.tenius.sns.repository;

import com.tenius.sns.domain.CommentLike;
import com.tenius.sns.domain.CommentLikeKey;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeKey> {
}
