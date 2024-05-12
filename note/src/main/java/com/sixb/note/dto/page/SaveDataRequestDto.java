package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveDataRequestDto {
    private String pageId;
    private String pageData;
}
