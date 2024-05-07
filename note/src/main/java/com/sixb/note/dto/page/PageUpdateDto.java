package com.sixb.note.dto.page;

import lombok.Data;

@Data
public class PageUpdateDto {
    private String pageId;
    private String template;
    private String color;
    private int direction;
}
