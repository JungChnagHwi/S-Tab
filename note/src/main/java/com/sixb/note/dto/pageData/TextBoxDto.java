package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextBoxDto {

	private List<TextDto> texts;
	private Float width;
	private Float height;
	private Float x;
	private Float y;

}
