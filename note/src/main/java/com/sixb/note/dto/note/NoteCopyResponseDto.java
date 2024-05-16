package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoteCopyResponseDto {
    private String noteId;
    private String title;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
