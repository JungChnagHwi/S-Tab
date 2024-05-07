package com.sixb.note.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Image")
public class Image {
    private String url;
    private Float width;
    private Float height;
    private Float x;
    private Float y;
}
