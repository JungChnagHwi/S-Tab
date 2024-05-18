package com.sixb.note.api.controller;

import com.sixb.note.api.service.PageService;
import com.sixb.note.dto.page.*;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.exception.PageNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/page")
@RequiredArgsConstructor
@Slf4j
public class PageController {

	private final PageService pageService;

	@PostMapping
	public ResponseEntity<?> createPage(@RequestBody PageCreateRequestDto request) {
		try {
			PageCreateResponseDto response = pageService.createPage(request.getBeforePageId());
			return ResponseEntity.ok(response);
		} catch (PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 생성에 실패했습니다.");
		}
	}

	@DeleteMapping("/{page-id}")
	public ResponseEntity<?> deletePage(@PathVariable("page-id") String pageId) {
		try {
			pageService.deletePage(pageId);
			return ResponseEntity.ok("페이지 삭제 완료");
		} catch (PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 생성에 실패했습니다.");
		}
	}

	// 페이지 양식 수정
	@PatchMapping
	public ResponseEntity<?> updatePage(@RequestBody PageUpdateDto request) {
		try {
			PageUpdateDto response = pageService.updatePage(request);
			return ResponseEntity.ok(response);
		} catch (PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 저장에 실패했습니다.");
		}
	}

	// 필기데이터 저장
	@PutMapping
	public ResponseEntity<?> saveData(@RequestBody SaveDataRequestDto request) {
		try {
			pageService.saveData(request);
			return ResponseEntity.ok("데이터 저장완료");
		} catch (NoteNotFoundException | PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 저장에 실패했습니다.");
		}
	}

	@GetMapping("/{note-id}")
	public ResponseEntity<?> getPageList(@PathVariable("note-id") String noteId, @RequestParam long userId) {
		try {
			PageListResponseDto response = pageService.getPageList(noteId, userId);
			return ResponseEntity.ok(response);
		} catch (NoteNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지를 불러오는데 실패했습니다.");
		}
	}

	@PostMapping("/copy")
	public ResponseEntity<?> copyPage(@RequestBody PageCopyRequestDto request) {
		try {
			PageInfoDto response = pageService.copyPage(request);
			return ResponseEntity.ok(response);
		} catch (NoteNotFoundException | PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 생성에 실패했습니다.");
		}
	}

	@PostMapping("/pdf")
	public ResponseEntity<?> pdfPage(@RequestBody PagePdfRequestDto request) {
		try {
			List<PageInfoDto> response = pageService.pdfPage(request);
			return ResponseEntity.ok(response);
		} catch (PageNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			log.error("Error: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("페이지 생성에 실패했습니다.");
		}
	}

}
