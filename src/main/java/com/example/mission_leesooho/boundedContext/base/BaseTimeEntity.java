package com.example.mission_leesooho.boundedContext.base;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) // @CreatedDate, @LastModifiedDate 작동하게 허용
public abstract class BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate // 아래 칼럼에는 값이 자동으로 들어간다.(INSERT 할 때)
    private LocalDateTime createDate;

    @LastModifiedDate // 아래 칼럼에는 값이 자동으로 들어간다.(UPDATE 할 때 마다)
    private LocalDateTime modifyDate;
}
