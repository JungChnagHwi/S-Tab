package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FolderResponseDto {
    private List<FolderInfo> folders;
    private List<NoteInfo> notes;

    @Getter
    @Setter
    public static class FolderInfo {
        private String folderId;
        private String title;
        private Boolean isLiked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isDeleted;
    }

    @Getter
    @Setter
    public static class NoteInfo {
        private String noteId;
        private String title;
        private int totalPageCnt;
        private Boolean isLiked;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isDeleted;
    }
}

