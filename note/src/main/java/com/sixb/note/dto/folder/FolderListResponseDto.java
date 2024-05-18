package com.sixb.note.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderListResponseDto {
	private List<FolderInfo> folders;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FolderInfo {
		private String folderId;
		private String title;
	}
}
