package com.sixb.note.dto.note;

import lombok.Data;

@Data
public class RelocateNoteRequestDto {

	private String noteId;
	private String parentFolderId;

}
