package com.sixb.note.dto.pageData;

import lombok.Data;

import java.util.List;

@Data
public class PathDto {
    private String penType;
    private Float strokeWidth;
    private String color;
    private List<CoordinateDto> coordinates;
}
