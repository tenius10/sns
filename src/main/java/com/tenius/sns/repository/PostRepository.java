package com.tenius.sns.repository;

import com.tenius.sns.domain.Post;
import com.tenius.sns.repository.search.PostSearch;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PostRepository extends JpaRepository<Post, Long>, PostSearch {
    @EntityGraph(attributePaths={"images"})
    @Query("select p from Post p where p.pno=:pno")
    Optional<Post> findByIdWithImages(@Param("pno") Long pno);
}
