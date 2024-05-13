package com.sixb.note.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sixb.note.api.service.PageService;
import com.sixb.note.dto.page.*;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.exception.PageNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
public class PageController {

    private final PageService pageService;

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

    // 페이지 양식 수정
    @PatchMapping("")
    public ResponseEntity<?> updatePage(@RequestBody PageUpdateDto request) {
        try {
            PageUpdateDto response = pageService.updatePage(request);
            return ResponseEntity.ok(response);
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 필기데이터 저장
    @PutMapping("")
    public ResponseEntity<?> saveData(@RequestBody SaveDataRequestDto request) {
        try {
            pageService.saveData(request);
            return ResponseEntity.ok("데이터 저장완료");
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{note-id}")
    public ResponseEntity<?> getPageList(@RequestParam("userId") long userId, @PathVariable("note-id") String noteId) {
        try {
            PageListResponseDto response = pageService.getPageList(userId, noteId);
            return ResponseEntity.ok(response);
        } catch (NoteNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (PageNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 페이지 링크 - 보류
//    @PostMapping("/link")
//    public ResponseEntity<?> linkPage(@RequestBody PageLinkRequestDto request) {
//        try {
//            pageService.linkPage(request);
//            return ResponseEntity.ok("링크 완료");
//        } catch (PageNotFoundException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PostMapping("/copy")
    public ResponseEntity<?> copyPage(PageCopyRequestDto request) throws PageNotFoundException {
        try {
            PageInfoDto response = pageService.copyPage(request);
            return ResponseEntity.ok(response);
        } catch (PageNotFoundException e) {
            throw new PageNotFoundException(e.getMessage());
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
