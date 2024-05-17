package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderListRequestDto {
	private String parentFolderId;
	private String folderId;
}
