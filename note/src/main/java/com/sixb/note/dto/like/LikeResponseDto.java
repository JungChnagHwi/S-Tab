package com.sixb.note.dto.like;

import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeResponseDto {
	private List<FolderInfo> folders;
	private List<NoteInfo> notes;
	private List<PageInfo> pages;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FolderInfo {
		private String folderId;
		private String rootFolderId;
		private String spaceId;
		private String spaceTitle;
		private String title;
		private LocalDateTime updatedAt;
		private LocalDateTime createdAt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class NoteInfo {
		private String noteId;
		private String spaceId;
		private String title;
		private String spaceTitle;
		private int totalPageCnt;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PageInfo {
		private String pageId;
		private String noteId;
		private String spaceId;
		private String noteTitle;
		private String spaceTitle;
		private String template;
		private String color;
		private int direction;
		private String pdfUrl;
		private int pdfPage;
		private List<PathDto> paths;
		private List<FigureDto> figures;
		private List<TextBoxDto> textBoxes;
		private List<ImageDto> images;
		private LocalDateTime createdAt;
		private LocalDateTime updatedAt;
	}

}
