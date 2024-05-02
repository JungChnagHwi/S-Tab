package com.sixb.note.api.controller;

import com.sixb.note.api.service.NoteService;
import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
