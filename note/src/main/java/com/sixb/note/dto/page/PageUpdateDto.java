package com.sixb.note.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageUpdateDto {
	private String pageId;
	private String template;
	private String color;
	private int direction;
}
