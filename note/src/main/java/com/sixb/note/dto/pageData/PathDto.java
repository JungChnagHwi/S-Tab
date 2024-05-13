package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathDto {

    private String penType;
    private Float strokeWidth;
    private String color;
    private List<CoordinateDto> coordinates;

}
