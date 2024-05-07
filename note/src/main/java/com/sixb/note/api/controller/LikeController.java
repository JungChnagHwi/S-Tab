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
    //public ResponseEntity<String> addLike(@RequestParam UUID userId, @RequestBody LikeRequestDto likeRequestDto)
    public ResponseEntity<String> addLike(@RequestBody LikeRequestDto likeRequestDto) {
        boolean result = likeService.addLike(likeRequestDto);
        //boolean result = likeService.addLike(userId, likeRequestDto);
        if (result) {
            return ResponseEntity.ok("즐겨찾기 추가 완료");
        } else {
            return ResponseEntity.badRequest().body("즐겨찾기 추가 실패");
        }
    }

    @GetMapping
    //public ResponseEntity<LikeResponseDto> getFavorites(@RequestParam UUID userId)
    public ResponseEntity<LikeResponseDto> getLikes() {
        LikeResponseDto likes = likeService.getLikes();
        return ResponseEntity.ok(likes);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<String> removeLike(@PathVariable String itemId) {
        boolean success = likeService.removeLike(itemId);
        if (success) {
            return ResponseEntity.ok("즐겨찾기 삭제 완료");
        } else {
            return ResponseEntity.badRequest().body("즐겨찾기 삭제 실패");
        }
    }


}
