package com.sixb.note.dto.page;

import com.sixb.note.dto.pageData.PageDataDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveDataRequestDto {
	private String pageId;
	private PageDataDto pageData;
}
