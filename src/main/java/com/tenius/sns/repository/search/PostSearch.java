package com.tenius.sns.repository.search;

import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostSearch {
    PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, String myUid);
    Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String myUid);
}