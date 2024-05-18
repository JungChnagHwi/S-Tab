package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

	private String url;
	private float width;
	private float height;
	private float x;
	private float y;

}
