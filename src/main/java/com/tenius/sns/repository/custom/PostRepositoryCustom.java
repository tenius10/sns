package com.tenius.sns.repository.custom;

import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;
import com.tenius.sns.dto.SearchOptionDTO;

import java.util.Optional;

public interface PostRepositoryCustom {
    Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String myUid);
    PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, SearchOptionDTO searchOptionDTO, String myUid);
}