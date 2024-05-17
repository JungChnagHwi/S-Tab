package com.sixb.note.dto.page;

import com.sixb.note.dto.pageData.PageDataDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveDataRequestDto {
	private String pageId;
	private PageDataDto pageData;
}
