package com.sixb.note.api.service;

import com.sixb.note.dto.Trash.TrashRequestDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrashService {
    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final PageRepository pageRepository;

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
        if (folder != null && folder.getIsDeleted() == true) {
            folder.setIsDeleted(false);
            folderRepository.save(folder);
            recovered = true;
        }

        // Recover Note
        Note note = noteRepository.findNoteById(itemId);
        if (note != null && note.getIsDeleted() == true) {
            note.setIsDeleted(false);
            noteRepository.save(note);
            recovered = true;
        }

        // Recover Page
        Page page = pageRepository.findPageById(itemId);
        if (page != null && page.getIsDeleted() == true) {
            page.setIsDeleted(false);
            pageRepository.save(page);
            recovered = true;
        }

        return recovered;
    }
}
