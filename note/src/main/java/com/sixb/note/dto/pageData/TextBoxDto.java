package com.sixb.note.dto.pageData;

import lombok.Data;

import java.util.List;

@Data
public class TextBoxDto {
    private List<TextDto> texts;
    private Float width;
    private Float height;
    private Float x;
    private Float y;
}
