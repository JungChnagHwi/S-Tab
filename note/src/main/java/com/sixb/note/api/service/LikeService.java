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

import java.util.List;
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
//        List<Folder> likedFolders = folderRepository.findAllLikedFoldersByUserId(userId);
//        List<Note> likedNotes = noteRepository.findAllLikedNotesByUserId(userId);
//        List<Page> likedPages = pageRepository.findAllLikedPagesByUserId(userId);
        String testUserId = "34840adb-99bc-4d78-a90a-c6d491b0bd62";
        List<Folder> likedFolders = folderRepository.findAllLikedFoldersByUserId(testUserId);
        List<Note> likedNotes = noteRepository.findAllLikedNotesByUserId(testUserId);
        List<Page> likedPages = pageRepository.findAllLikedPagesByUserId(testUserId);

//        User user = userRepository.findUserById(testUserId);
        return new LikeResponseDto(likedFolders, likedNotes, likedPages);
    }

    //즐겨찾기 삭제
    //public boolean removeLike(UUID userId, UUID itemId)
    public boolean removeLike(String itemId) {
        String testUserId = "34840adb-99bc-4d78-a90a-c6d491b0bd62";
        User user = userRepository.findUserById(testUserId);

            if (folderRepository.findFolderById(itemId) != null) {
                folderRepository.deleteLikeFolder(testUserId, itemId);
                return true;
            } else if (noteRepository.findNoteById(itemId) != null) {
                noteRepository.deleteLikeNote(testUserId, itemId);
                return true;
            } else if (pageRepository.findPageById(itemId) != null) {
                pageRepository.deleteLikePage(testUserId, itemId);
                return true;
            }

        return false;
    }

}
