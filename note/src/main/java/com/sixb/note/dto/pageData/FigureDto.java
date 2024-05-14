package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FigureDto {

    private String shape;
    private String color;
    private Float width;
    private Float height;
    private Float x;
    private Float y;

}
