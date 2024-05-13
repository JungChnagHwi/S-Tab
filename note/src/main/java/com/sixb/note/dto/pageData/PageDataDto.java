package com.sixb.note.dto.pageData;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PageDataDto {
    private List<PathDto> paths;
    private List<FigureDto> figures;
    private List<TextBoxDto> textBoxes;
    private List<ImageDto> images;

    @JsonCreator
    public PageDataDto(@JsonProperty("paths") List<PathDto> paths,
                       @JsonProperty("figures") List<FigureDto> figures,
                       @JsonProperty("textBoxes") List<TextBoxDto> textBoxes,
                       @JsonProperty("images") List<ImageDto> images) {
        this.paths = paths;
        this.figures = figures;
        this.textBoxes = textBoxes;
        this.images = images;
    }
}
