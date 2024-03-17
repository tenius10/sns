package com.tenius.sns.repository.custom;

import com.tenius.sns.dto.FollowDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;

public interface FollowRepositoryCustom {
    PageResponseDTO<FollowDTO> findAllFollowersByFolloweeUid(PageRequestDTO pageRequestDTO, String followeeUid, String myUid);
    PageResponseDTO<FollowDTO> findAllFollowingsByFollowerUid(PageRequestDTO pageRequestDTO, String followerUid, String myUid);
}
