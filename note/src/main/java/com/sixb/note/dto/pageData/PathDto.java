package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Path")
public class PathDto {
    private String penType;
    private Float strokeWidth;
    private String color;
    private List<CoordinateDto> coordinates;
}
