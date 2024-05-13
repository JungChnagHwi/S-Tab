package com.sixb.note.dto.folder;

import lombok.Data;

@Data
public class RelocateFolderRequestDto {

	private String folderId;
	private String parentFolderId;

}
