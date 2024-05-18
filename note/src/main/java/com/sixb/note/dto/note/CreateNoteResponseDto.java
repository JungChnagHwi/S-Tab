package com.sixb.note.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteResponseDto {
	private String noteId;
	private String title;
	private int totalPageCnt;
	@Builder.Default
	private Boolean isLiked = false;
	private PageDto page;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@Builder.Default
	private Boolean isDeleted = false;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PageDto {
		private String pageId;
		private String color;
		private String template;
		private int direction;
		@Builder.Default
		private Boolean isBookmarked = false;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@Builder.Default
		private Boolean isDeleted = false;
	}
}
