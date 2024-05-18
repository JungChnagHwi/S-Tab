package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateFolderResponseDto {
	private String folderId;
	private String title;
	private boolean isLiked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean isDeleted;
}
