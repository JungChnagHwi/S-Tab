package com.sixb.note.dto.pageData;

import lombok.Data;

@Data
public class TextDto {
    private String content;
    private String font;
    private Float size;
    private String color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
}
