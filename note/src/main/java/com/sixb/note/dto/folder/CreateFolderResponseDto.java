package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateFolderResponseDto {
	private String folderId;
	private String title;
	private Boolean isLiked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean isDeleted;
}
