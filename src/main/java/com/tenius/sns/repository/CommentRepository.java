package com.tenius.sns.repository;

import com.tenius.sns.domain.*;
import com.tenius.sns.repository.search.CommentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentSearch {
}
