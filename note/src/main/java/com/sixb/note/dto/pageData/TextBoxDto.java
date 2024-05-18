package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextBoxDto {

	private List<TextDto> texts;
	private float width;
	private float height;
	private float x;
	private float y;

}
