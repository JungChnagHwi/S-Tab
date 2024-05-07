package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Figure")
public class Figure {
    private String shape;
    private String color;
    private Float width;
    private Float height;
    private Float x;
    private Float y;

}
