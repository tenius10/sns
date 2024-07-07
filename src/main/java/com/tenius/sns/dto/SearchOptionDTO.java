package com.tenius.sns.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOptionDTO {
    private String keyword;  // 특정 키워드 관련 게시글 검색
    private String relatedUid;  // 특정 유저 관련 게시글 검색 (본인이 작성 or 좋아요 클릭한 게시글)
}
