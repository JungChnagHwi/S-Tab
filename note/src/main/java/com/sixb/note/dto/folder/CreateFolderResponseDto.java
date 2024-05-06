package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateFolderResponseDto {
    private String folderId;
    private String title;
    private Boolean isLiked;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private int isDelete;
}
