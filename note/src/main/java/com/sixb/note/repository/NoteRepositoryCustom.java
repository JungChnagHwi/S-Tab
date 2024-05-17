package com.sixb.note.repository;

import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.exception.FolderNotFoundException;

import java.util.*;

public interface NoteRepositoryCustom {

	Optional<CreateNoteResponseDto> createNote(CreateNoteRequestDto request) throws FolderNotFoundException;

	void deleteNote(String noteId);

}
