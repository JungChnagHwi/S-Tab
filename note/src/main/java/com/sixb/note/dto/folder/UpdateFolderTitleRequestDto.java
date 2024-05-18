package com.sixb.note.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFolderTitleRequestDto {
	private String folderId;
	private String newTitle;
}
