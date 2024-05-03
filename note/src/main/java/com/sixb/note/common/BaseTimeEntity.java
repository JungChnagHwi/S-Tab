package com.sixb.note.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
//@EnableJpaAuditing
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

	private int isDelete;

}
