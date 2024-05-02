package com.sixb.note.api.service;

import com.sixb.note.dto.folder.FolderResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.SpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public FolderResponseDto getFolderDetail(UUID folderId) {
        List<Folder> folders = folderRepository.findSubFoldersByFolderId(folderId);
        List<Note> notes = noteRepository.findNotesByFolderId(folderId);

        List<FolderResponseDto.FolderInfo> folderInfos = folders.stream().map(folder -> {
            FolderResponseDto.FolderInfo info = new FolderResponseDto.FolderInfo();
            info.setFolderId(folder.getId());
            info.setTitle(folder.getTitle());
            info.setCreateAt(folder.getCreatedAt());
            info.setUpdateAt(folder.getModifiedAt());
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
            info.setIsLiked(false);
            return info;
        }).collect(Collectors.toList());

        FolderResponseDto responseDto = new FolderResponseDto();
        responseDto.setFolders(folderInfos);
        responseDto.setNotes(noteInfos);
        return responseDto;
    }


}
