package com.sixb.note.api.controller;

import com.sixb.note.api.service.PageService;
import com.sixb.note.dto.page.PageCreateRequestDto;
import com.sixb.note.dto.page.PageCreateResponseDto;
import com.sixb.note.exception.PageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
public class PageController {
    private PageService pageService;

    @PostMapping("")
    public ResponseEntity<?> createPage(@RequestBody PageCreateRequestDto request) {
        try {
            PageCreateResponseDto response = pageService.createPage(request);
            return ResponseEntity.ok(response);
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{page-id}")
    public ResponseEntity<?> deletePage(@PathVariable("page-id") String pageId) {
        try {
            pageService.deletePage(pageId);
            return ResponseEntity.ok("페이지 삭제 완료");
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
