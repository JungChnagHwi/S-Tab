package com.sixb.note.api.controller;

import com.sixb.note.api.service.TrashService;
import com.sixb.note.dto.Trash.TrashRequestDto;
import com.sixb.note.dto.Trash.TrashResponseDto;
import com.sixb.note.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/trash")
@RequiredArgsConstructor
public class TrashController {

	private final TrashService trashService;

	@GetMapping
	public ResponseEntity<TrashResponseDto> getDeletedItems(long userId) {
		TrashResponseDto deletedItems = trashService.findDeletedItems(userId);
		return ResponseEntity.ok(deletedItems);
	}

	@PatchMapping
	public ResponseEntity<String> recoverItem(@RequestBody TrashRequestDto trashRequestDto) {
		try {
			trashService.recoverItem(trashRequestDto);
			return ResponseEntity.ok("복원 완료");
		} catch (NotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}
