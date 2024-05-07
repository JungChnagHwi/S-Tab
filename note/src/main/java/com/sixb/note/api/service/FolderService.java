package com.sixb.note.api.service;


import com.sixb.note.dto.folder.CreateFolderRequestDto;
import com.sixb.note.dto.folder.CreateFolderResponseDto;
import com.sixb.note.dto.folder.FolderResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Space;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.util.IdCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FolderService {

    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private UserService userService;

    // 폴더 조회
    public FolderResponseDto getFolderDetail(String folderId) {
        List<Folder> folders = folderRepository.findSubFoldersByFolderId(folderId);
        List<Note> notes = noteRepository.findNotesByFolderId(folderId);

        List<FolderResponseDto.FolderInfo> folderInfos = folders.stream().map(folder -> {
            FolderResponseDto.FolderInfo info = new FolderResponseDto.FolderInfo();
            info.setFolderId(folder.getId());
            info.setTitle(folder.getTitle());
            info.setCreateAt(folder.getCreatedAt());
            info.setUpdateAt(folder.getModifiedAt());
            info.setIsDelete(folder.getIsDelete());
            info.setIsLiked(false);
            return info;
        }).collect(Collectors.toList());

        List<FolderResponseDto.NoteInfo> noteInfos = notes.stream().map(note -> {
            FolderResponseDto.NoteInfo info = new FolderResponseDto.NoteInfo();
            info.setNoteId(note.getId());
            info.setTitle(note.getTitle());
            info.setTotalPageCnt(note.getTotalPageCnt());
            info.setCreateAt(note.getCreatedAt());
            info.setUpdateAt(note.getModifiedAt());
            info.setIsDelete(note.getIsDelete());
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
        newFolder.setId(formattedFolderId);

        // 부모 폴더 또는 스페이스 ID로 조회
        Folder parentFolder = folderRepository.findFolderById(request.getParentFolderId());
        Space space = null;
        if (parentFolder == null) {
            // 폴더가 아니면 스페이스 조회
            space = spaceRepository.findSpaceById(request.getParentFolderId());
            if (space == null) {
                throw new IllegalStateException("Neither parent folder nor space found for given ID");
            }
            newFolder.setSpaceId(space.getId());
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
        newFolder.setModifiedAt(now);

        folderRepository.save(newFolder);
        if (parentFolder != null) {
            folderRepository.save(parentFolder);
        } else if (space != null) {
            spaceRepository.save(space);
        }

        CreateFolderResponseDto response = new CreateFolderResponseDto();
        response.setFolderId(newFolder.getId());
        response.setTitle(newFolder.getTitle());
        response.setCreateAt(LocalDateTime.now());
        response.setUpdateAt(LocalDateTime.now());
        response.setIsDelete(0);
        response.setIsLiked(false);
        return response;
    }

    //폴더 이름 수정
    public boolean updateFolderTitle(String folderId, String newTitle) {
        Folder folder = folderRepository.findFolderById(folderId);
        if (folder != null) {
            folder.setTitle(newTitle);
            folderRepository.save(folder);
            return true;
        }
        return false;
    }

    //폴더 삭제
    public boolean deleteFolder(String folderId) {
        Folder folder = folderRepository.findFolderById(folderId);
        if (folder != null) {
            folder.setIsDelete(1);
            folderRepository.save(folder);
            return true;
        }
        return false;
    }
}
