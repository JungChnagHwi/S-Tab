package com.sixb.note.dto.folder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFolderRequestDto {

	private String parentFolderId;
	private String title;

}
