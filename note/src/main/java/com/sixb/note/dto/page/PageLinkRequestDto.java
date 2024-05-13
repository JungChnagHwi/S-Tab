package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PageLinkRequestDto {
    private String linkPageId;
    private String targetPageId;
}
