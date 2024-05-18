package com.sixb.note.dto.folder;

import com.fasterxml.jackson.annotation.JsonProperty;
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
		@JsonProperty(value = "isLiked")
		private boolean isLiked;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@JsonProperty(value = "isDeleted")
		private boolean isDeleted;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NoteInfo {
		private String noteId;
		private String title;
		private int totalPageCnt;
		@JsonProperty(value = "isLiked")
		private boolean isLiked;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@JsonProperty(value = "isDeleted")
		private boolean isDeleted;
	}
}

