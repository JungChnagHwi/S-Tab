package com.sixb.note.api.controller;

import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.*;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.exception.SpaceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.UriEncoder;

@RestController
@RequestMapping("/api/folder")
@RequiredArgsConstructor
public class FolderController {

	private final FolderService folderService;

	@GetMapping("/{folderId}")
	public ResponseEntity<?> getFolderById(@PathVariable("folderId") String folderId, @RequestParam long userId) {
		try {
			FolderResponseDto folderInfo = folderService.getFolderDetail(folderId, userId);
			return ResponseEntity.ok(folderInfo);
		} catch (FolderNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@GetMapping("/name")
	public ResponseEntity<FolderResponseDto> getFolderByName(@RequestParam long userId, @RequestParam String name, @RequestParam String spaceId) {
		FolderResponseDto response = folderService.getFolderByName(userId, UriEncoder.decode(name), spaceId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/space/{spaceId}")
	public ResponseEntity<FolderResponseDto> getSpaceById(@PathVariable("spaceId") String spaceId) {
		FolderResponseDto spaceInfo = folderService.getSpaceDetail(spaceId);
		return ResponseEntity.ok(spaceInfo);
	}

	@PostMapping
	public ResponseEntity<?> createFolder(@RequestBody CreateFolderRequestDto request) {
		try {
			CreateFolderResponseDto createdFolder = folderService.createFolder(request);
			return ResponseEntity.ok(createdFolder);
		} catch (FolderNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/rename")
	public ResponseEntity<String> updateFolderTitle(@RequestBody UpdateFolderTitleRequestDto request) {
		try {
			folderService.updateFolderTitle(request.getFolderId(), request.getNewTitle());
			return ResponseEntity.ok("폴더 이름 수정 완료");
		} catch (FolderNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/relocation")
	public ResponseEntity<?> relocateFolder(@RequestBody RelocateFolderRequestDto request) {
		folderService.relocateFolder(request);
		return ResponseEntity.ok("폴더 위치 변경 완료");
	}

	@DeleteMapping("/{folderId}")
	public ResponseEntity<String> deleteFolder(@PathVariable("folderId") String folderId) {
		folderService.deleteFolder(folderId);
		return ResponseEntity.ok("폴더 삭제 완료");
	}

	@PostMapping("/list")
	public ResponseEntity<?> getFoldersBetween(@RequestBody FolderListRequestDto requestDto) {
		try {
			FolderListResponseDto responseDto = folderService.getFoldersBetween(requestDto);
			return ResponseEntity.ok(responseDto);
		} catch (FolderNotFoundException | SpaceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
