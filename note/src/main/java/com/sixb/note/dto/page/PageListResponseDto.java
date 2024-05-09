package com.sixb.note.dto.page;

import com.sixb.note.entity.Page;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageListResponseDto {
    private List<PageInfoDto> data;
}
