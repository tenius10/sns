package com.tenius.sns.repository.search;

import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;

import java.util.Optional;

public interface PostSearch {
    PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, String uid);
    Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String uid);
}
