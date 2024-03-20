package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int size=10;  //페이지 사이즈
    private String keyword;  //검색어
    private Long cursor;  //커서
    private String criteria;  //정렬 기준 (최신, 등록, 인기 등)
}