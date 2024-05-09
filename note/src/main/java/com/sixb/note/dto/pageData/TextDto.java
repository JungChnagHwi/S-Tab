package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Text")
public class TextDto {
    private String content;
    private String font;
    private Float size;
    private String color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
}
