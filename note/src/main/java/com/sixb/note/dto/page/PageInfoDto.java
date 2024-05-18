package com.sixb.note.dto.page;

import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfoDto {
	private String pageId;
	private String noteId;
	private String color;
	private String template;
	private int direction;
	private boolean isBookmarked;
	private String pdfUrl;
	private int pdfPage;
	private String createdAt;
	private String updatedAt;
	private List<PathDto> paths;
	private List<FigureDto> figures;
	private List<TextBoxDto> textBoxes;
	private List<ImageDto> images;
}
