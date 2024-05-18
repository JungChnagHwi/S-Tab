package com.sixb.note.dto.note;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteTitleRequestDto {
	private String noteId;
	private String newTitle;
}
