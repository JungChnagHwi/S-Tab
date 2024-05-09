package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class PageCreateRequestDto {
    private String beforePageId;
}
