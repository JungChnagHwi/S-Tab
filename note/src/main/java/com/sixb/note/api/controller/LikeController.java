package com.sixb.note.api.controller;

import com.sixb.note.api.service.LikeService;
import com.sixb.note.dto.Like.LikeRequestDto;
import com.sixb.note.dto.Like.LikeResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Autowired
    private LikeService likeService;

    @PostMapping
    public ResponseEntity<String> addLike(@RequestParam long userId, @RequestBody LikeRequestDto likeRequestDto) {
        boolean result = likeService.addLike(userId, likeRequestDto);
        if (result) {
            return ResponseEntity.ok("즐겨찾기 추가 완료");
        } else {
            return ResponseEntity.badRequest().body("즐겨찾기 추가 실패");
        }
    }

    @GetMapping
    public ResponseEntity<LikeResponseDto> getFavorites(@RequestParam long userId) {
        LikeResponseDto likes = likeService.getLikes(userId);
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeLike(@RequestParam long userId, @PathVariable String itemId) {
        boolean success = likeService.removeLike(userId, itemId);
        if (success) {
            return ResponseEntity.ok("즐겨찾기 삭제 완료");
        } else {
            return ResponseEntity.badRequest().body("즐겨찾기 삭제 실패");
        }
    }


}
