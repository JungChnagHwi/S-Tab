package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateFolderTitleRequestDto {
	private String folderId;
	private String newTitle;
}
