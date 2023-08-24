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
    private Pageable pageable= PageRequest.of(0, 10, Sort.by("regDate").descending());;
    private CursorDTO cursor;

    public LocalDateTime getPivot(){
        return cursor==null? null : cursor.getRegDate();
    }
}
