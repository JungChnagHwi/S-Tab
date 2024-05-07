package com.sixb.note.api.service;


import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.dto.note.UpdateNoteTitleRequestDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Space;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.util.IdCreator;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private SpaceRepository spaceRepository;

    public CreateNoteResponseDto createNote(CreateNoteRequestDto request) {
        // 새로운 노트 생성
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setTotalPageCnt(1);// 초기 페이지 수는 1으로 설정

        String formattedNoteId = IdCreator.create("n");
//        UUID formattedNoteId = UUID.randomUUID();
        note.setId(formattedNoteId);
        // 새로운 페이지 생성
        Page page = new Page();
        page.setTemplate(String.valueOf(request.getTemplate()));
        page.setColor(String.valueOf(request.getColor()));
        page.setDirection(request.getDirection());

        LocalDateTime now = LocalDateTime.now();
        page.setCreatedAt(now);
        page.setModifiedAt(now);
        note.setCreatedAt(now);
        note.setModifiedAt(now);

        // 노트와 페이지 연결
        note.setPages(new ArrayList<>());
        note.getPages().add(page);

        String parentFolderId = request.getParentFolderId();

        if (parentFolderId != null) {
            // 부모 폴더 ID가 주어진 경우
            Folder optionalFolder = folderRepository.findFolderById(parentFolderId);
            if (optionalFolder!=null) {
                // 부모 폴더가 있는 경우 노트를 해당 폴더에 연결
                Folder parentFolder = optionalFolder;
                parentFolder.getNotes().add(note);
                folderRepository.save(parentFolder);
            } else {
                // 부모 폴더를 찾지 못한 경우에 대한 처리
                String spaceId = parentFolderId;
                if (spaceId != null) {
                    // spaceId로 스페이스를 찾아서 노트를 연결
                    Space optionalSpace = spaceRepository.findSpaceById(spaceId);
                    if (optionalSpace != null) {
                        optionalSpace.getNotes().add(note);
                        spaceRepository.save(optionalSpace);
                    }
                }
            }
        }
        // 저장
        noteRepository.save(note);


        // 응답 DTO 구성
        CreateNoteResponseDto response = new CreateNoteResponseDto();
        response.setNoteId(note.getId());
        response.setTitle(note.getTitle());
        response.setTotalPageCnt(note.getTotalPageCnt());
        response.setLiked(false);
        response.setCreateAt(LocalDateTime.now());
        response.setUpdateAt(LocalDateTime.now());
        response.setIsDelete(0);

        // 페이지 DTO 설정
        CreateNoteResponseDto.PageDto pageDto = new CreateNoteResponseDto.PageDto();
        pageDto.setPageId(page.getId());
        pageDto.setColor(request.getColor());
        pageDto.setTemplate(request.getTemplate());
        pageDto.setDirection(request.getDirection());
        pageDto.setBookmarked(false);
        pageDto.setCreateAt(LocalDateTime.now());
        pageDto.setUpdateAt(LocalDateTime.now());
        pageDto.setIsDelete(0);

        response.setPage(pageDto);

        return response;
    }

    //노트 이름 변경
    public boolean updateNoteTitle(String noteId, String newTitle) {
        Note note = noteRepository.findNoteById(noteId);
        if (note != null) {
            note.setTitle(newTitle);
            noteRepository.save(note);
            return true;
        }
        return false;
    }

    //노트 삭제
    public boolean deleteNote(String noteId) {
        Note note = noteRepository.findNoteById(noteId);
        if (note != null) {
            note.setIsDelete(1);
            noteRepository.save(note);
            return true;
        }
        return false;
    }
}
