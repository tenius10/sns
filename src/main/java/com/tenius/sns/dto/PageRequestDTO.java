package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int size=10;
    private Long no;  //cursor 의 식별자가 Long 인 경우
    private String id;  //cursor 의 식별자가 String 인 경우
    private CursorDTO cursor;

    public Pageable getPageable(){
        return PageRequest.of(0, size, Sort.by("regDate").descending());
    }
}