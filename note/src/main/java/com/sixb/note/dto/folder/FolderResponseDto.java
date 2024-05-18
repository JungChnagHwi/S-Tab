package com.sixb.note.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponseDto {
	private List<FolderInfo> folders;
	private List<NoteInfo> notes;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FolderInfo {
		private String folderId;
		private String title;
		@Builder.Default
		private Boolean isLiked = false;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@Builder.Default
		private Boolean isDeleted = false;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NoteInfo {
		private String noteId;
		private String title;
		private int totalPageCnt;
		@Builder.Default
		private Boolean isLiked = false;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@Builder.Default
		private Boolean isDeleted = false;
	}
}

