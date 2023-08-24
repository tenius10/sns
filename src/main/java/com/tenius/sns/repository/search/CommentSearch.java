package com.tenius.sns.repository.search;

import com.tenius.sns.dto.CommentWithCountDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

public interface CommentSearch {
    PageResponseDTO<CommentWithCountDTO> search(Long pno, PageRequestDTO pageRequestDTO);
}
