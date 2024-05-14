package com.sixb.note.api.service;

import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.dto.note.RelocateNoteRequestDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.exception.NoteNotFoundException;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.util.Const;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NoteService {

	private final NoteRepository noteRepository;
	private final FolderRepository folderRepository;

	@Transactional
	public CreateNoteResponseDto createNote(CreateNoteRequestDto request) throws FolderNotFoundException {
		String parentFolderId = request.getParentFolderId();
		Folder parentFolder = folderRepository.findFolderById(parentFolderId);

		if (parentFolder == null) {
			throw new FolderNotFoundException("존재하지 않는 폴더입니다.");
		}

		LocalDateTime now = LocalDateTime.now();
		String formattedNoteId = IdCreator.create("n");

		// 새로운 페이지 생성
		Page page = Page.builder()
				.pageId(IdCreator.create("p"))
				.noteId(formattedNoteId)
				.template(request.getTemplate())
				.color(request.getColor())
				.direction(request.getDirection())
				.pageData(Const.INIT_PAGE_DATA)
				.build();

		// 새로운 노트 생성
		Note note = Note.builder()
				.title(request.getTitle())
				.totalPageCnt(1)
				.noteId(formattedNoteId)
				.createdAt(now)
				.updatedAt(now)
				// 노트와 페이지 연결
				.page(page)
				.build();

		// 노트를 부모 폴더에 연결
		parentFolder.getNotes().add(note);
		folderRepository.save(parentFolder);

		// 저장
		noteRepository.save(note);

		// 응답 DTO 구성
		return CreateNoteResponseDto.builder()
				.noteId(note.getNoteId())
				.title(note.getTitle())
				.totalPageCnt(note.getTotalPageCnt())
				.isLiked(false)
				.createdAt(note.getCreatedAt())
				.createdAt(note.getUpdatedAt())
				.isDeleted(false)
				.page(CreateNoteResponseDto.PageDto.builder()
						.pageId(page.getPageId())
						.color(page.getColor())
						.template(page.getTemplate())
						.direction(page.getDirection())
						.isBookmarked(false)
						.createdAt(page.getCreatedAt())
						.updatedAt(page.getUpdatedAt())
						.isDeleted(false)
						.build())
				.build();
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
