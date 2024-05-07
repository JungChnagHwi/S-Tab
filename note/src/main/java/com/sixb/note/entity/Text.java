package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Text")
public class Text {
    private String content;
    private String font;
    private Float size;
    private String color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;
}
