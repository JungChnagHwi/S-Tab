package com.sixb.note.api.service;

import com.sixb.note.dto.Trash.TrashRequestDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TrashService {
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private PageRepository pageRepository;

    //휴지통 조회
    public List<Object> findDeletedItems() {
        List<Object> deletedItems = new ArrayList<>();
        deletedItems.addAll(folderRepository.findDeletedFolders());
        deletedItems.addAll(noteRepository.findDeletedNotes());
        deletedItems.addAll(pageRepository.findDeletedPages());
        return deletedItems;
    }

    //휴지통 복원
    public boolean recoverItem(TrashRequestDto trashRequestDto) {
        String itemId = trashRequestDto.getId();
        boolean recovered = false;

        // Recover Folder
        Folder folder = folderRepository.findFolderById(itemId);
        if (folder != null && folder.getIsDelete() == 1) {
            folder.setIsDelete(0);
            folderRepository.save(folder);
            recovered = true;
        }

        // Recover Note
        Note note = noteRepository.findNoteById(itemId);
        if (note != null && note.getIsDelete() == 1) {
            note.setIsDelete(0);
            noteRepository.save(note);
            recovered = true;
        }

        // Recover Page
        Page page = pageRepository.findPageById(itemId);
        if (page != null && page.getIsDelete() == 1) {
            page.setIsDelete(0);
            pageRepository.save(page);
            recovered = true;
        }

        return recovered;
    }
}
