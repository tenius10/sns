package com.tenius.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@AllArgsConstructor
@NoArgsConstructor
@Getter
abstract class BaseEntity {
    @CreatedDate
    @Column(name="reg_date", updatable=false)
    private LocalDateTime regDate;
    @LastModifiedDate
    @Column(name="mod_date")
    private LocalDateTime modDate;
}
