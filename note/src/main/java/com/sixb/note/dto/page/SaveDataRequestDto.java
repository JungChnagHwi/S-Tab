package com.sixb.note.dto.page;

import com.sixb.note.entity.Figure;
import com.sixb.note.entity.Image;
import com.sixb.note.entity.Path;
import com.sixb.note.entity.TextBox;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SaveDataRequestDto {
    private String pageId;
    private List<Path> paths;
    private List<Figure> figures;
    private List<TextBox> textBoxes;
    private List<Image> images;
}
