package com.sixb.note.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageListResponseDto {
	private String title;
	private List<PageInfoDto> data;
}
