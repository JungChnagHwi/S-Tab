package com.sixb.note.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@Document(collection = "PageData")
public class PageData {
    @Id
    private String id;
    private List<Path> paths;
    private List<Figure> figures;
    private List<TextBox> textBoxes;
    private List<Image> images;
}
