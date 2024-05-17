package com.sixb.note.dto.like;

import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class LikeResponseDto {
	private List<FolderInfo> folders;
	private List<Note> notes;
	private List<Page> pages;

	@Getter
	@Setter
	public static class FolderInfo {
		private String spaceTitle;
		private String folderId;
		private String rootFolderId;
		private String title;
		private LocalDateTime updatedAt;
		private LocalDateTime createdAt;
	}

	public LikeResponseDto(List<FolderInfo> folders, List<Note> notes, List<Page> pages) {
		this.folders = folders;
		this.notes = notes;
		this.pages = pages;
	}

}
