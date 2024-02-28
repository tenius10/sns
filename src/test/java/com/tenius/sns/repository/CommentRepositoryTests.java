package com.tenius.sns.repository;

import com.tenius.sns.dto.CommentWithStatusDTO;
import com.tenius.sns.dto.PageRequestDTO;
import com.tenius.sns.dto.PageResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class CommentRepositoryTests {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    public void testPagingByCursor(){
        Long pno=104L;
        String uid="x3SzQoEkSRwDnspp";
        PageRequestDTO pageRequestDTO= PageRequestDTO.builder().build();
        PageResponseDTO<CommentWithStatusDTO> result=commentRepository.search(pageRequestDTO, pno, uid);
        log.info("다음 페이지 존재 여부: "+result.isHasNext());
        result.getContent().forEach(commentDTO->log.info(commentDTO));
    }
}
