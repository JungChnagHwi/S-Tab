package com.sixb.note.api.controller;

import com.sixb.note.api.service.LikeService;
import com.sixb.note.dto.Like.LikeRequestDto;
import com.sixb.note.dto.Like.LikeResponseDto;
import com.sixb.note.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {

	private final LikeService likeService;

	@PostMapping
	public ResponseEntity<String> addLike(@RequestParam long userId, @RequestBody LikeRequestDto likeRequestDto) {
		try {
			likeService.addLike(userId, likeRequestDto.getId());
			return ResponseEntity.ok("즐겨찾기 추가 완료");
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{itemId}")
	public ResponseEntity<String> removeLike(@RequestParam long userId, @PathVariable String itemId) {
		try {
			likeService.removeLike(userId, itemId);
			return ResponseEntity.ok("즐겨찾기 삭제 완료");
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<LikeResponseDto> getLikes(@RequestParam long userId) {
		LikeResponseDto likes = likeService.getLikes(userId);
		return ResponseEntity.ok(likes);
	}

}
