package com.sixb.note.dto.page;


import lombok.Data;

@Data
public class PageCopyRequestDto {
	private String beforePageId;
	private String targetPageId;
}
