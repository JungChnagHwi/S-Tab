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
public class PathDto {

	private String penType;
	private float strokeWidth;
	private String color;
	private List<CoordinateDto> coordinates;

}
