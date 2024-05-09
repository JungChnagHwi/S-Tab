package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Figure")
public class FigureDto {
    private String shape;
    private String color;
    private Float width;
    private Float height;
    private Float x;
    private Float y;

}
