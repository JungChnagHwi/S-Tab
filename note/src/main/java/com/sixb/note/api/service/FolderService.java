package com.sixb.note.api.service;

import com.sixb.note.dto.folder.*;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Space;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class FolderService {

	private final FolderRepository folderRepository;
	private final NoteRepository noteRepository;
	private final SpaceRepository spaceRepository;

	// 폴더 조회
	public FolderResponseDto getFolderDetail(String folderId, long userId) {
		List<Folder> folders = folderRepository.findSubFoldersByFolderId(folderId);
		List<Note> notes = noteRepository.findNotesByFolderId(folderId);

		// 폴더와 노트 ID 목록을 추출
		List<String> folderIds = folders.stream().map(Folder::getFolderId).collect(Collectors.toList());
		List<String> noteIds = notes.stream().map(Note::getNoteId).collect(Collectors.toList());

		// 좋아요 상태를 가져옴
		List<String> likedFolderIds = folderRepository.findLikedFolderIdsByUserId(userId, folderIds);
		List<String> likedNoteIds = noteRepository.findLikedNoteIdsByUserId(userId, noteIds);

		List<FolderResponseDto.FolderInfo> folderInfos = folders.stream().map(folder -> {
			FolderResponseDto.FolderInfo info = new FolderResponseDto.FolderInfo();
			info.setFolderId(folder.getFolderId());
			info.setTitle(folder.getTitle());
			info.setCreatedAt(folder.getCreatedAt());
			info.setUpdatedAt(folder.getUpdatedAt());
			info.setIsDeleted(folder.getIsDeleted());
			info.setIsLiked(likedFolderIds.contains(folder.getFolderId()));
			return info;
		}).collect(Collectors.toList());

		List<FolderResponseDto.NoteInfo> noteInfos = notes.stream().map(note -> {
			FolderResponseDto.NoteInfo info = new FolderResponseDto.NoteInfo();
			info.setNoteId(note.getNoteId());
			info.setTitle(note.getTitle());
			info.setTotalPageCnt(note.getTotalPageCnt());
			info.setCreatedAt(note.getCreatedAt());
			info.setUpdatedAt(note.getUpdatedAt());
			info.setIsDeleted(note.getIsDeleted());
			info.setIsLiked(likedNoteIds.contains(note.getNoteId()));
			return info;
		}).collect(Collectors.toList());

		FolderResponseDto responseDto = new FolderResponseDto();
		responseDto.setFolders(folderInfos);
		responseDto.setNotes(noteInfos);
		return responseDto;
	}

	public FolderResponseDto getSpaceDetail(String spaceId) {
		List<Folder> folders = folderRepository.findFoldersBySpaceId(spaceId);
		List<Note> notes = noteRepository.findNotesBySpaceId(spaceId);

		List<FolderResponseDto.FolderInfo> folderInfos = folders.stream().map(folder -> {
			FolderResponseDto.FolderInfo info = new FolderResponseDto.FolderInfo();
			info.setFolderId(folder.getFolderId());
			info.setTitle(folder.getTitle());
			info.setCreatedAt(folder.getCreatedAt());
			info.setUpdatedAt(folder.getUpdatedAt());
			info.setIsDeleted(folder.getIsDeleted());
			info.setIsLiked(false);
			return info;
		}).collect(Collectors.toList());

		List<FolderResponseDto.NoteInfo> noteInfos = notes.stream().map(note -> {
			FolderResponseDto.NoteInfo info = new FolderResponseDto.NoteInfo();
			info.setNoteId(note.getNoteId());
			info.setTitle(note.getTitle());
			info.setTotalPageCnt(note.getTotalPageCnt());
			info.setCreatedAt(note.getCreatedAt());
			info.setUpdatedAt(note.getUpdatedAt());
			info.setIsDeleted(note.getIsDeleted());
			info.setIsLiked(false);
			return info;
		}).collect(Collectors.toList());

		FolderResponseDto responseDto = new FolderResponseDto();
		responseDto.setFolders(folderInfos);
		responseDto.setNotes(noteInfos);
		return responseDto;
	}

	//폴더 생성
	public CreateFolderResponseDto createFolder(CreateFolderRequestDto request) throws FolderNotFoundException {
		// 부모 폴더 찾기
		Folder newFolder = new Folder();
		newFolder.setTitle(request.getTitle());

		String formattedFolderId = IdCreator.create("f");
		newFolder.setFolderId(formattedFolderId);

		// 부모 폴더 또는 스페이스 ID로 조회
		Folder parentFolder = folderRepository.findFolderById(request.getParentFolderId())
				.orElseThrow(() -> new FolderNotFoundException("존재하지 않는 폴더입니다."));

		newFolder.setSpaceId(parentFolder.getSpaceId());
		List<Folder> subFolders = parentFolder.getSubFolders();
		if (subFolders == null) {
			subFolders = new ArrayList<>();
		}
		subFolders.add(newFolder);
		parentFolder.setSubFolders(subFolders);

		LocalDateTime now = LocalDateTime.now();
		newFolder.setCreatedAt(now);
		newFolder.setUpdatedAt(now);

		folderRepository.save(newFolder);
		folderRepository.save(parentFolder);

		CreateFolderResponseDto response = new CreateFolderResponseDto();
		response.setFolderId(newFolder.getFolderId());
		response.setTitle(newFolder.getTitle());
		response.setCreatedAt(LocalDateTime.now());
		response.setUpdatedAt(LocalDateTime.now());
		response.setIsDeleted(false);
		response.setIsLiked(false);
		return response;
	}

	//폴더 이름 수정
	public void updateFolderTitle(String folderId, String newTitle) throws FolderNotFoundException {
		folderRepository.findFolderById(folderId)
				.orElseThrow(() -> new FolderNotFoundException("존재하지 않는 폴더입니다."));

		folderRepository.updateFolderTitle(folderId, newTitle);
	}

	public void relocateFolder(RelocateFolderRequestDto request) {
		folderRepository.relocateFolder(request.getFolderId(), request.getParentFolderId());
	}

	//폴더 삭제
	public void deleteFolder(String folderId) {
		LocalDateTime now = LocalDateTime.now();
		folderRepository.deleteFolder(folderId, now);
	}

	public FolderListResponseDto getFoldersBetween(FolderListRequestDto requestDto) {
		List<Folder> folders = folderRepository.findFoldersBetween(requestDto.getParentFolderId(), requestDto.getFolderId());
		List<FolderListResponseDto.FolderInfo> folderInfos = new ArrayList<>();

		for (int i = 0; i < folders.size(); i++) {
			Folder folder = folders.get(i);
			FolderListResponseDto.FolderInfo info = new FolderListResponseDto.FolderInfo();
			info.setFolderId(folder.getFolderId());

			if (i == 0) {
				Optional<Space> space = spaceRepository.findSpaceById(folder.getSpaceId());
				space.ifPresent(s -> info.setTitle(s.getTitle()));
			} else {
				info.setTitle(folder.getTitle());
			}

			folderInfos.add(info);
		}

		FolderListResponseDto responseDto = new FolderListResponseDto();
		responseDto.setFolders(folderInfos);
		return responseDto;
	}

	public FolderResponseDto getFolderByName(long userId, String name, String spaceId) {
		return folderRepository.getFolderByName(userId, name, spaceId);
	}

}
