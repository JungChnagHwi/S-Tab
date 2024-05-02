package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateNoteResponseDto {
    private UUID noteId;
    private String title;
    private int totalPageCnt;
    private boolean isLiked;
    private PageDto page;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Getter
    @Setter
    public static class PageDto {
        private UUID pageId;
        private int color;
        private int template;
        private int direction;
        private boolean isBookmarked;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;
    }
}
