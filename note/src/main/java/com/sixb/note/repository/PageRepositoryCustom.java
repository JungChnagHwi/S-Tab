package com.sixb.note.repository;


import com.sixb.note.dto.page.PageCreateRequestDto;
import com.sixb.note.dto.page.PageCreateResponseDto;
import com.sixb.note.entity.Page;

public interface PageRepositoryCustom {
//    boolean isLastPage(String pageId);
    void connectNextPage(String pageId,String newPageId);
//    PageCreateResponseDto createNextPage(PageCreateRequestDto pageCreateRequestDto);
//    Page getNextPageId(String pageId);
//    Page findFirstPageByNoteId(String noteId);
}
