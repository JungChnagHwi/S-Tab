package com.sixb.note.api.service;

import com.sixb.note.dto.Like.LikeResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.entity.User;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LikeService {

	private final UserRepository userRepository;
	private final FolderRepository folderRepository;
	private final NoteRepository noteRepository;
	private final PageRepository pageRepository;

	// 즐겨찾기 추가
	public void addLike(long userId, String itemId) throws NotFoundException {
		modifyLike(userId, itemId, true);
	}

	// 즐겨찾기 삭제
	public void removeLike(long userId, String itemId) throws NotFoundException {
		modifyLike(userId, itemId, false);
	}

	// 즐겨찾기 추가/삭제
	private void modifyLike(long userId, String itemId, boolean isAdding) throws NotFoundException {
		User user = userRepository.findUserById(userId);

		if (user == null) {
			throw new NotFoundException("존재하지 않는 유저입니다.");
		}

		Object item = findItemById(itemId);

		if (item == null) {
			throw new NotFoundException("존재하지 않는 아이템입니다.");
		}

		if (isAdding) {
			addItemToUser(user, item);
			userRepository.save(user);
		} else {
			removeItemLike(userId, itemId, item);
		}
	}

	private Object findItemById(String itemId) {
		return switch (itemId.charAt(0)) {
			case 'f' -> folderRepository.findFolderById(itemId);
			case 'n' -> noteRepository.findNoteById(itemId);
			case 'p' -> pageRepository.findPageById(itemId);
			default -> throw new IllegalArgumentException("잘못된 요청입니다.");
		};
	}

	private void addItemToUser(User user, Object item) {
		if (item instanceof Folder) {
			user.getFolders().add((Folder) item);
		} else if (item instanceof Note) {
			user.getNotes().add((Note) item);
		} else if (item instanceof Page) {
			user.getPages().add((Page) item);
		}
	}

	private void removeItemLike(long userId, String itemId, Object item) {
		if (item instanceof Folder) {
			folderRepository.deleteLikeFolder(userId, itemId);
		} else if (item instanceof Note) {
			noteRepository.deleteLikeNote(userId, itemId);
		} else if (item instanceof Page) {
			pageRepository.deleteLikePage(userId, itemId);
		} else {
			throw new IllegalArgumentException("잘못된 요청입니다.");
		}
	}

	//즐겨찾기 조회
	public LikeResponseDto getLikes(long userId) {
		List<Folder> likedFolders = folderRepository.findAllLikedFoldersByUserId(userId);
		List<Note> likedNotes = noteRepository.findAllLikedNotesByUserId(userId);
		List<Page> likedPages = pageRepository.findAllLikedPagesByUserId(userId);

		return new LikeResponseDto(likedFolders, likedNotes, likedPages);
	}

}
