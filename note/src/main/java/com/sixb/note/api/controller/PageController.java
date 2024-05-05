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

    @PostMapping()
    public ResponseEntity<?> createPage(@RequestParam("userId") long userId,
                                        @RequestBody PageCreateRequestDto request) {
        try {
            PageCreateResponseDto response = pageService.createPage(userId, request);
            return ResponseEntity.ok(response);
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
