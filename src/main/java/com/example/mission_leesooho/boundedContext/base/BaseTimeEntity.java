package com.example.mission_leesooho.boundedContext.base;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // @CreatedDate, @LastModifiedDate 작동하게 허용
public class BaseTimeEntity {

    @CreatedDate // 아래 칼럼에는 값이 자동으로 들어간다.(INSERT 할 때)
    @Column(name = "create_date")
    private LocalDateTime createDate;

    @LastModifiedDate // 아래 칼럼에는 값이 자동으로 들어간다.(UPDATE 할 때 마다)
    @Column(name = "modify_date")
    private LocalDateTime modifyDate;
}
