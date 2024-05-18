package com.sixb.note.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageLinkRequestDto {
	private String linkPageId;
	private String targetPageId;
}
