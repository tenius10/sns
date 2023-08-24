package com.tenius.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private boolean hasNext;
    private T cursor;

    public static class PageResponseDTOBuilder<T> {
        public PageResponseDTOBuilder content(List<T> content){
            this.content=content;
            if(content.size()>0) this.cursor=content.get(content.size()-1);
            return this;
        }
    }
}
