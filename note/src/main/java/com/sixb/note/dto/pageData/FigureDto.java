package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FigureDto {

	private String shape;
	private String color;
	private float width;
	private float height;
	private float x;
	private float y;

}
