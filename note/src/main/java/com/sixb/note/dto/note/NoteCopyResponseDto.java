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
public class NoteCopyResponseDto {
	private String noteId;
	private String title;
	@Builder.Default
	private Boolean isLiked = false;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
