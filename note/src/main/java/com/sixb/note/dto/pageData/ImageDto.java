package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    private String url;
    private Float width;
    private Float height;
    private Float x;
    private Float y;

}
