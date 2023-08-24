package com.tenius.sns.repository.search;

import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithCountDTO;

import java.util.Optional;

public interface PostSearch {
    PageResponseDTO<PostWithCountDTO> search(PageRequestDTO pageRequestDTO);
    Optional<PostWithCountDTO> findByIdWithAll(Long pno);
}
