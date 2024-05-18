package com.sixb.note.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagePdfRequestDto {
	private String beforePageId;
	private String pdfUrl;
	private int pdfPageCount; // pdf 총 페이지 수
}
