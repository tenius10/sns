package com.tenius.sns.repository.custom;

import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import com.tenius.sns.dto.PostWithStatusDTO;

import java.util.Optional;

public interface PostRepositoryCustom {
    PageResponseDTO<PostWithStatusDTO> search(PageRequestDTO pageRequestDTO, String writerUid, String myUid);
    Optional<PostWithStatusDTO> findByIdWithAll(Long pno, String myUid);
}