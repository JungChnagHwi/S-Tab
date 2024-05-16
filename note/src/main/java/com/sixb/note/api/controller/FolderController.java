package com.sixb.note.api.controller;

import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.*;
import com.sixb.note.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
public class FolderController {

	private final FolderService folderService;

	@GetMapping("/{folderId}")
	public ResponseEntity<FolderResponseDto> getFolderById(@PathVariable("folderId") String folderId, @RequestParam long userId) {
		FolderResponseDto folderInfo = folderService.getFolderDetail(folderId, userId);
		return ResponseEntity.ok(folderInfo);
	}

	@GetMapping("/name")
	public ResponseEntity<FolderResponseDto> getFolderByName(@RequestParam long userId, @RequestParam String name, @RequestParam String spaceId) {
		FolderResponseDto response = folderService.getFolderByName(userId, name, spaceId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/space/{spaceId}")
	public ResponseEntity<FolderResponseDto> getSpaceById(@PathVariable("spaceId") String spaceId) {
		FolderResponseDto spaceInfo = folderService.getSpaceDetail(spaceId);
		return ResponseEntity.ok(spaceInfo);
	}

	@PostMapping
	public ResponseEntity<CreateFolderResponseDto> createFolder(@RequestBody CreateFolderRequestDto request) {
		CreateFolderResponseDto createdFolder = folderService.createFolder(request);
		return ResponseEntity.ok(createdFolder);
	}

	@PatchMapping("/rename")
	public ResponseEntity<String> updateFolderTitle(@RequestBody UpdateFolderTitleRequestDto request) {
		try {
			folderService.updateFolderTitle(request.getFolderId(), request.getNewTitle());
			return ResponseEntity.ok("폴더 이름 수정 완료");
		} catch (NotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PatchMapping("/relocation")
	public ResponseEntity<?> relocateFolder(@RequestBody RelocateFolderRequestDto request) {
		folderService.relocateFolder(request);
		return ResponseEntity.ok("폴더 위치 변경 완료");
	}

	@DeleteMapping("/{folderId}")
	public ResponseEntity<String> deleteFolder(@PathVariable("folderId") String folderId) {
		boolean isUpdated = folderService.deleteFolder(folderId);
		if (isUpdated) {
			return ResponseEntity.ok("폴더 삭제 완료");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/list")
	public ResponseEntity<FolderListResponseDto> getFoldersBetween(@RequestBody FolderListRequestDto requestDto) {
		FolderListResponseDto responseDto = folderService.getFoldersBetween(requestDto);
		return ResponseEntity.ok(responseDto);
	}
}
