package com.tenius.sns.repository;

import com.tenius.sns.domain.Post;
import com.tenius.sns.repository.search.PostSearch;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long>, PostSearch {
}
