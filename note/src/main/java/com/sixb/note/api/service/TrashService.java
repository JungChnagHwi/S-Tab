package com.sixb.note.api.service;

import com.sixb.note.dto.Trash.TrashRequestDto;
import com.sixb.note.dto.Trash.TrashResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TrashService {

	private final FolderRepository folderRepository;
	private final NoteRepository noteRepository;
	private final PageRepository pageRepository;

	//휴지통 조회
	public TrashResponseDto findDeletedItems(long userId) {
		List<Folder> deletedFolders = folderRepository.findDeletedFolders(userId);
		List<Note> deletedNotes = noteRepository.findDeletedNotes(userId);
		List<Page> deletedPages = pageRepository.findDeletedPages(userId);

		return new TrashResponseDto(deletedFolders, deletedNotes, deletedPages);
	}

	// 휴지통 복원
	public void recoverItem(TrashRequestDto trashRequestDto) throws NotFoundException {
		String itemId = trashRequestDto.getId();
		Object item = findItemById(itemId);

		if (item == null) {
			throw new NotFoundException("아이템이 존재하지 않습니다.");
		}

		recoverItem(item);
	}

	private Object findItemById(String itemId) {
		return switch (itemId.charAt(0)) {
			case 'f' -> folderRepository.findFolderById(itemId);
			case 'n' -> noteRepository.findNoteById(itemId);
			case 'p' -> pageRepository.findPageById(itemId);
			default -> throw new IllegalArgumentException("잘못된 요청입니다.");
		};
	}

	private void recoverItem(Object item) {
		if (item instanceof Folder folder) {
			if (folder.getIsDeleted()) {
				folder.setIsDeleted(false);
				folderRepository.save(folder);
			}
		} else if (item instanceof Note note) {
			if (note.getIsDeleted()) {
				note.setIsDeleted(false);
				noteRepository.save(note);
			}
		} else if (item instanceof Page page) {
			if (page.getIsDeleted()) {
				page.setIsDeleted(false);
				pageRepository.save(page);
			}
		}
	}

}
