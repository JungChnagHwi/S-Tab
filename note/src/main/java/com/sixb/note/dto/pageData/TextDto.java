package com.sixb.note.dto.pageData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDto {

    private String content;
    private String font;
    private Float size;
    private String color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underline;

}
