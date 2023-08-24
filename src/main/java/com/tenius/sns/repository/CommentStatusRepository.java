package com.tenius.sns.repository;

import com.tenius.sns.domain.CommentStatus;
import com.tenius.sns.domain.CommentStatusKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentStatusRepository extends JpaRepository<CommentStatus, CommentStatusKey> {
    /**
     * 댓글 상태가 존재하면, 데이터베이스에 저장.
     * 댓글 상태가 존재하지 않으면, 데이터베이스에서 삭제.
     * @param commentStatus
     * @return
     */
    default void saveWithCheck(CommentStatus commentStatus) {
        if(commentStatus.existsStatus()){
            save(commentStatus);
        }
        else{
            CommentStatusKey key=CommentStatusKey.builder()
                    .cno(commentStatus.getCno())
                    .uid(commentStatus.getUid())
                    .build();
            deleteById(key);
        }
    }
}
