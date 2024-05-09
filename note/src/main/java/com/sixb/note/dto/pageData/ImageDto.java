package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Image")
public class ImageDto {
    private String url;
    private Float width;
    private Float height;
    private Float x;
    private Float y;
}
