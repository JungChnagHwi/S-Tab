package com.sixb.note.dto.pageData;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Coordinate")
public class CoordinateDto {
    private Float x;
    private Float y;
}
