package com.sixb.note.api.controller;


import com.sixb.note.api.service.NoteService;
import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.dto.note.UpdateNoteTitleRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/api/note")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping
    public ResponseEntity<CreateNoteResponseDto> createNote(@RequestBody CreateNoteRequestDto request) {
        CreateNoteResponseDto response = noteService.createNote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/rename")
    public ResponseEntity<String> updateNoteTitle(@RequestBody UpdateNoteTitleRequestDto request) {
        boolean isUpdated = noteService.updateNoteTitle(request.getNoteId(), request.getNewTitle());
        if (isUpdated) {
            return ResponseEntity.ok("노트 이름 수정 완료");
        } else {
            return ResponseEntity.badRequest().body("노트 이름 수정 실패");
        }
    }

    @PatchMapping("/{noteId}")
    public ResponseEntity<String> deleteFolder(@PathVariable("noteId") String noteId) {
        boolean isUpdated = noteService.deleteNote(noteId);
        if (isUpdated) {
            return ResponseEntity.ok("노트 삭제 완료");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
