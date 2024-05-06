package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PageCreateRequestDto {
    private String beforePageId;
}
