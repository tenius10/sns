package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPageDTO {
    private UserInfoDTO userInfo;
    @Builder.Default
    private long postCount = 0;
    @Builder.Default
    private long followerCount=0;
    @Builder.Default
    private long followingCount=0;
    private PageResponseDTO<PostWithStatusDTO> postPage;
}
