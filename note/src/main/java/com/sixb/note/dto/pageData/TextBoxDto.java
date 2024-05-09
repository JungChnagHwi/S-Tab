package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "TextBox")
public class TextBoxDto {
    private List<TextDto> texts;
    private Float width;
    private Float height;
    private Float x;
    private Float y;
}
