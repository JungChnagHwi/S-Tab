package com.sixb.note.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFolderResponseDto {
	private String folderId;
	private String title;
	@Builder.Default
	private Boolean isLiked = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@Builder.Default
	private Boolean isDeleted = false;
}
