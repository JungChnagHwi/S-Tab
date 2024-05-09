package com.sixb.note.dto.page;

import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SaveDataRequestDto {
    private String pageId;
    private List<PathDto> paths;
    private List<FigureDto> figures;
    private List<TextBoxDto> textBoxes;
    private List<ImageDto> images;
}
