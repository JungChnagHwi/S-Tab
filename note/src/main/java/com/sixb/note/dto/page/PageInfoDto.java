package com.sixb.note.dto.page;

import com.sixb.note.dto.pageData.FigureDto;
import com.sixb.note.dto.pageData.ImageDto;
import com.sixb.note.dto.pageData.PathDto;
import com.sixb.note.dto.pageData.TextBoxDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Cacheable
public class PageInfoDto {
    private String pageId;
    private String noteId;
    private String color;
    private String template;
    private int direction;
    private Boolean isBookmarked;
    private String pdfUrl;
    private Integer pdfPage;
    private LocalDateTime updatedAt;
    private List<PathDto> paths;
    private List<FigureDto> figures;
    private List<TextBoxDto> textBoxes;
    private List<ImageDto> images;
}
