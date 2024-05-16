package com.sixb.note.api.service;

import com.sixb.note.dto.note.*;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService {

	private final NoteRepository noteRepository;
	private final FolderRepository folderRepository;
	private final PageRepository pageRepository;

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

	public NoteCopyResponseDto copyNote(NoteCopyRequestDto requestDto) {
		Note existingNote = noteRepository.findNoteById(requestDto.getNoteId());
		Folder parentFolder = folderRepository.findFolderById(requestDto.getParentFolderId());
		String formattedNoteId = IdCreator.create("n");

		List<Page> pages = pageRepository.findAllByNoteId(requestDto.getNoteId());
		Page newPageHead = copyPages(pages, formattedNoteId);

		Note newNote = Note.builder()
				.noteId(formattedNoteId)
				.spaceId(parentFolder.getSpaceId())
				.title(requestDto.getTitle())
				.totalPageCnt(existingNote.getTotalPageCnt())
				.page(newPageHead)  // 복사된 페이지의 첫 번째 페이지
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		noteRepository.save(newNote);

		parentFolder.getNotes().add(newNote);
		folderRepository.save(parentFolder);

		NoteCopyResponseDto responseDto = new NoteCopyResponseDto();
		responseDto.setNoteId(newNote.getNoteId());
		responseDto.setTitle(newNote.getTitle());
		responseDto.setLiked(false);  
		responseDto.setCreatedAt(newNote.getCreatedAt());
		responseDto.setUpdatedAt(newNote.getUpdatedAt());

		return responseDto;
	}

	private Page copyPages(List<Page> pages, String newNoteId) {
		Page previousPage = null;
		Page firstNewPage = null;

		for (Page existingPage : pages) {
			Page newPage = Page.builder()
					.pageId(IdCreator.create("p"))
					.noteId(newNoteId)  // 새로운 Note ID를 설정
					.template(existingPage.getTemplate())
					.color(existingPage.getColor())
					.direction(existingPage.getDirection())
					.pdfUrl(existingPage.getPdfUrl())
					.pdfPage(existingPage.getPdfPage())
					.pageData(existingPage.getPageData())
					.build();

			newPage = pageRepository.save(newPage);

			if (previousPage != null) {
				previousPage.setNextPage(newPage);
				pageRepository.save(previousPage);
			} else {
				firstNewPage = newPage;
			}

			previousPage = newPage;
		}

		return firstNewPage;
	}
}
