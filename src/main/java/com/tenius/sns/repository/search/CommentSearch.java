package com.tenius.sns.repository.search;

import com.tenius.sns.dto.CommentWithStatusDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

import java.util.Optional;

public interface CommentSearch {
    PageResponseDTO<CommentWithStatusDTO> search(PageRequestDTO pageRequestDTO, Long pno, String myUid);
    Optional<CommentWithStatusDTO> findByIdWithAll(Long cno, String myUid);
}
