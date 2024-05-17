package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteCopyRequestDto {
	private String noteId;
	private String parentFolderId;
	private String title;
}
