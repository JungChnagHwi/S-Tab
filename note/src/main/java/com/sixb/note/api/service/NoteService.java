package com.sixb.note.api.service;

import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.dto.note.RelocateNoteRequestDto;
import com.sixb.note.entity.Note;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteService {

	private final NoteRepository noteRepository;

	@Transactional
	public CreateNoteResponseDto createNote(CreateNoteRequestDto request) throws FolderNotFoundException {
		return noteRepository.createNote(request)
				.orElseThrow(() -> new IllegalArgumentException("노트 생성에 실패했습니다."));
	}

	//노트 이름 변경
	public void updateNoteTitle(String noteId, String newTitle) throws NotFoundException {
		Note note = noteRepository.findNoteById(noteId);
		if (note == null) {
			throw new NotFoundException("노트 이름 수정 실패");
		}
		noteRepository.updateNoteTitle(noteId, newTitle);
	}

	public void relocateNote(RelocateNoteRequestDto request) {
		noteRepository.relocateNote(request.getNoteId(), request.getParentFolderId());
	}

	//노트 삭제
	public void deleteNote(String noteId) throws NoteNotFoundException {
		Note note = noteRepository.findNoteById(noteId);

		if (note == null) {
			throw new NoteNotFoundException("존재하지 않는 노트입니다.");
		}

		note.setIsDeleted(true);
		noteRepository.save(note);
	}

}
