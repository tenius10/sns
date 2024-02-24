package com.tenius.sns.repository.search;

import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostSearch {
    PageResponseDTO<PostWithStatusDTO> search(Pageable pageable, LocalDateTime cursor, String uid);
    Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String uid);
}