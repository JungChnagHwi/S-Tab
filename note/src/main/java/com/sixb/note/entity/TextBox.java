package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "TextBox")
public class TextBox {
    private List<Text> texts;
    private Float width;
    private Float height;
    private Float x;
    private Float y;
}
