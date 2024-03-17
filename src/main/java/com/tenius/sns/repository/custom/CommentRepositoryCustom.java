package com.tenius.sns.repository.custom;

import com.tenius.sns.dto.CommentWithStatusDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

import java.util.Optional;

public interface CommentRepositoryCustom {
    PageResponseDTO<CommentWithStatusDTO> search(PageRequestDTO pageRequestDTO, Long pno, String myUid);
    Optional<CommentWithStatusDTO> findByIdWithAll(Long cno, String myUid);
}
