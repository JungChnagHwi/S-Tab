package com.sixb.note.dto.page;

import lombok.Data;

@Data
public class PagePdfRequestDto {
	private String beforePageId;
	private String pdfUrl;
	private int pdfPageCount; // pdf 총 페이지 수
}
