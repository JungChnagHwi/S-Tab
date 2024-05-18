package com.sixb.note.api.controller;

import com.sixb.note.api.service.NoteService;
import com.sixb.note.dto.note.*;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.exception.NoteNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/note")
@RequiredArgsConstructor
public class NoteController {

	private final NoteService noteService;

	@PostMapping
	public ResponseEntity<?> createNote(@RequestBody CreateNoteRequestDto request) {
		try {
			CreateNoteResponseDto response = noteService.createNote(request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (FolderNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PatchMapping("/rename")
	public ResponseEntity<String> updateNoteTitle(@RequestBody UpdateNoteTitleRequestDto request) {
		try {
			noteService.updateNoteTitle(request.getNoteId(), request.getNewTitle());
			return ResponseEntity.ok("노트 이름 수정 완료");
		} catch (NoteNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PatchMapping("/relocation")
	public ResponseEntity<?> relocateNote(@RequestBody RelocateNoteRequestDto request) {
		noteService.relocateNote(request);
		return ResponseEntity.ok("노트 위치 변경 완료");
	}

	@DeleteMapping("/{noteId}")
	public ResponseEntity<String> deleteNote(@PathVariable("noteId") String noteId) {
		noteService.deleteNote(noteId);
		return ResponseEntity.ok("노트 삭제 완료");
	}

	@PostMapping("/copy")
	public ResponseEntity<?> copyNote(@RequestBody NoteCopyRequestDto requestDto) {
		try {
			NoteCopyResponseDto responseDto = noteService.copyNote(requestDto);
			return ResponseEntity.ok(responseDto);
		} catch (NoteNotFoundException | FolderNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

}
