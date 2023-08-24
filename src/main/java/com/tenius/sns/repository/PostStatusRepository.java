package com.tenius.sns.repository;

import com.tenius.sns.domain.PostStatus;
import com.tenius.sns.domain.PostStatusKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostStatusRepository extends JpaRepository<PostStatus, PostStatusKey> {
    /**
     * 게시글 상태가 존재하면, 데이터베이스에 저장.
     * 게시글 상태가 존재하지 않으면, 데이터베이스에서 삭제.
     * @param postStatus
     * @return
     */
    default void saveWithCheck(PostStatus postStatus) {
        if(postStatus.existsStatus()){
            save(postStatus);
        }
        else{
            PostStatusKey key=PostStatusKey.builder()
                    .pno(postStatus.getPno())
                    .uid(postStatus.getUid())
                    .build();
            deleteById(key);
        }
    }
}
