package com.sixb.note.dto.page;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PageCreateResponseDto {
    private String pageId;
    private String template;
    private String color;
    private int direction;
    private LocalDateTime updatedAt;
}
