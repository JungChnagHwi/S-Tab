package com.sixb.note.dto.note;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty(value = "isLiked")
	private boolean isLiked;
	private PageDto page;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@JsonProperty(value = "isDeleted")
	private boolean isDeleted;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PageDto {
		private String pageId;
		private String color;
		private String template;
		private int direction;
		@JsonProperty(value = "isBookmarked")
		private boolean isBookmarked;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
		@JsonProperty(value = "isDeleted")
		private boolean isDeleted;
	}
}
