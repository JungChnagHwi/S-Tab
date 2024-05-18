package com.sixb.note.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageCreateResponseDto {
	private String pageId;
	private String template;
	private String color;
	private int direction;
	private LocalDateTime updatedAt;
}
