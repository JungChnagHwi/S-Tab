package com.sixb.note.api.service;

import com.sixb.note.dto.Like.LikeRequestDto;
import com.sixb.note.dto.Like.LikeResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import com.sixb.note.entity.User;
import com.sixb.note.repository.FolderRepository;
import com.sixb.note.repository.NoteRepository;
import com.sixb.note.repository.PageRepository;
import com.sixb.note.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LikeService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FolderRepository folderRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private PageRepository pageRepository;

    //즐겨찾기 추가
    //public boolean addLike(UUID userId, LikeRequestDto likeRequestDto)
    public boolean addLike(LikeRequestDto likeRequestDto) {
        //유저 id 하드코딩
        String testUserId = "34840adb-99bc-4d78-a90a-c6d491b0bd62";
        User user = userRepository.findUserById(testUserId);
//        User user = userRepository.findUserById(userId);
        if (user == null) return false;

        String itemId = likeRequestDto.getId();
        boolean added = false;

        Folder folder = folderRepository.findFolderById(itemId);
        if (folder != null) {
            user.getFolders().add(folder);
            added = true;
        }

        Note note = noteRepository.findNoteById(itemId);
        if (note != null) {
            user.getNotes().add(note);
            added = true;
        }

        Page page = pageRepository.findPageById(itemId);
        if (page != null) {
            user.getPages().add(page);
            added = true;
        }

        userRepository.save(user);
        return added;
    }

    //즐겨찾기 조회
    //public LikeResponseDto getFavorites(UUID userId)
    public LikeResponseDto getLikes() {
        String testUserId = "34840adb-99bc-4d78-a90a-c6d491b0bd62";
        User user = userRepository.findUserById(testUserId);
        return new LikeResponseDto(user.getFolders(), user.getNotes(), user.getPages());
    }

    //즐겨찾기 삭제
    //public boolean removeLike(UUID userId, UUID itemId, String itemType)
    public boolean removeLike(String itemId) {
        String testUserId = "34840adb-99bc-4d78-a90a-c6d491b0bd62";
        User user = userRepository.findUserById(testUserId);

        boolean removed = false;

        Folder folder = folderRepository.findFolderById(itemId);
        if (folder != null) {
            removed = user.getFolders().removeIf(f -> f.getId().equals(itemId));
        }

        if (!removed) {
            Note note = noteRepository.findNoteById(itemId);
            if (note != null) {
                removed = user.getNotes().removeIf(n -> n.getId().equals(itemId));
            }
        }

        if (!removed) {
            Page page = pageRepository.findPageById(itemId);
            if (page != null) {
                removed = user.getPages().removeIf(p -> p.getId().equals(itemId));
            }
        }

        if (removed) {
            userRepository.save(user);
        }

        return removed;
    }

}
