package com.sixb.note.dto.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSpaceTitleRequestDto {
	private String spaceId;
	private String newTitle;
}
