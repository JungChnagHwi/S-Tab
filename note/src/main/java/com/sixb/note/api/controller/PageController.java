package com.sixb.note.api.controller;

import com.sixb.note.api.service.PageService;
import com.sixb.note.dto.page.PageCreateRequestDto;
import com.sixb.note.dto.page.PageCreateResponseDto;
import com.sixb.note.exception.InvalidTokenException;
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
    public ResponseEntity<?> createPage(@RequestHeader("Authorization") String token,
                                        @RequestBody PageCreateRequestDto request) {
        try {
            PageCreateResponseDto response = pageService.createPage(token, request);
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (PageNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
