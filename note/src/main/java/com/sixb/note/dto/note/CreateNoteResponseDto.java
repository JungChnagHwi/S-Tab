package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateNoteResponseDto {
    private String noteId;
    private String title;
    private int totalPageCnt;
    private boolean isLiked;
    private PageDto page;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;

    @Getter
    @Setter
    public static class PageDto {
        private String pageId;
        private String color;
        private String template;
        private int direction;
        private boolean isBookmarked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isDeleted;
    }
}
