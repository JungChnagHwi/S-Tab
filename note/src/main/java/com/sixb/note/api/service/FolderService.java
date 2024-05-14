package com.sixb.note.api.service;


import com.sixb.note.dto.folder.CreateFolderRequestDto;
import com.sixb.note.dto.folder.CreateFolderResponseDto;
import com.sixb.note.dto.folder.FolderResponseDto;
import com.sixb.note.dto.folder.RelocateFolderRequestDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Space;
import com.sixb.note.exception.NotFoundException;
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

	private final SpaceRepository spaceRepository;
	private final FolderRepository folderRepository;
	private final NoteRepository noteRepository;

	// 폴더 조회
	public FolderResponseDto getFolderDetail(String folderId) {
		List<Folder> folders = folderRepository.findSubFoldersByFolderId(folderId);
		List<Note> notes = noteRepository.findNotesByFolderId(folderId);

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
	public CreateFolderResponseDto createFolder(CreateFolderRequestDto request) {
		// 부모 폴더 찾기
		Folder newFolder = new Folder();
		newFolder.setTitle(request.getTitle());

		String formattedFolderId = IdCreator.create("f");
//        UUID formattedFolderId = UUID.randomUUID();
		newFolder.setFolderId(formattedFolderId);

		// 부모 폴더 또는 스페이스 ID로 조회
		Folder parentFolder = folderRepository.findFolderById(request.getParentFolderId());
		Space space = null;
		if (parentFolder == null) {
			// 폴더가 아니면 스페이스 조회
			space = spaceRepository.findSpaceById(request.getParentFolderId());
			if (space == null) {
				throw new IllegalStateException("Neither parent folder nor space found for given ID");
			}
			newFolder.setSpaceId(space.getSpaceId());
			space.getFolders().add(newFolder);
		} else {
			newFolder.setSpaceId(parentFolder.getSpaceId());
			List<Folder> subFolders = parentFolder.getSubFolders();
			if (subFolders == null) {
				subFolders = new ArrayList<>();
			}
			subFolders.add(newFolder);
			parentFolder.setSubFolders(subFolders);
		}
		LocalDateTime now = LocalDateTime.now();
		newFolder.setCreatedAt(now);
		newFolder.setUpdatedAt(now);

		folderRepository.save(newFolder);
		if (parentFolder != null) {
			folderRepository.save(parentFolder);
		} else if (space != null) {
			spaceRepository.save(space);
		}

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
	public void updateFolderTitle(String folderId, String newTitle) throws NotFoundException {
		Folder folder = folderRepository.findFolderById(folderId);
		if (folder == null) {
			throw new NotFoundException("폴더 이름 수정 실패");
		}
		folderRepository.updateFolderTitle(folderId, newTitle);
	}

	public void relocateFolder(RelocateFolderRequestDto request) {
		folderRepository.relocateFolder(request.getFolderId(), request.getParentFolderId());
	}

	//폴더 삭제
	public boolean deleteFolder(String folderId) {
		Folder folder = folderRepository.findFolderById(folderId);
		if (folder != null) {
			folder.setIsDeleted(true);
			folderRepository.save(folder);
			return true;
		}
		return false;
	}

}
