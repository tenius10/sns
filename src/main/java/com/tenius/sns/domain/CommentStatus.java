package com.tenius.sns.domain;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "comment_status", indexes={
        @Index(name="idx_comment_status_cno_uid", columnList = "cno, uid")
})
@IdClass(CommentStatusKey.class)
public class CommentStatus extends BaseEntity {
    @Id
    private Long cno;
    @Id
    private String uid;
    private boolean liked;
    private boolean hided;
    @JoinColumn(name="cno", insertable = false, updatable = false)
    @ManyToOne(fetch= FetchType.LAZY)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Comment comment;
    @JoinColumn(name="uid", insertable = false, updatable = false)
    @ManyToOne(fetch= FetchType.LAZY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private UserInfo user;

    public void changeLiked(boolean liked){
        this.liked=liked;
    }

    /**
     * 댓글에 상태(좋아요, 숨김)가 존재하는지 여부 확인
     * @return 상태 존재 여부
     */
    public boolean existsStatus(){
        return liked || hided;
    }
}
