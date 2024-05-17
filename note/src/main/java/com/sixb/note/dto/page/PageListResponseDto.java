package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;

import java.util.*;

@Data
@Builder
public class PageListResponseDto {
	private String title;
	private List<PageInfoDto> data;
}
