package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextDto {

	private String content;
	private String font;
	private float size;
	private String color;
	private boolean bold;
	private boolean italic;
	private boolean underline;

}
