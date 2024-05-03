package com.sixb.note.api.controller;

<<<<<<< HEAD
import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.CreateFolderRequestDto;
import com.sixb.note.dto.folder.CreateFolderResponseDto;
import com.sixb.note.dto.folder.FolderResponseDto;
import com.sixb.note.dto.folder.UpdateFolderTitleRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/folder")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @GetMapping("/{folderId}")
    public ResponseEntity<FolderResponseDto> getFolderById(@PathVariable("folderId") UUID folderId) {
        FolderResponseDto folderInfo = folderService.getFolderDetail(folderId);
        return ResponseEntity.ok(folderInfo);
    }

    @PostMapping
    public ResponseEntity<CreateFolderResponseDto> createFolder(@RequestBody CreateFolderRequestDto request) {
        CreateFolderResponseDto createdFolder = folderService.createFolder(request);
        return ResponseEntity.ok(createdFolder);
    }

    @PatchMapping("/rename")
    public ResponseEntity<String> updateFolderTitle(@RequestBody UpdateFolderTitleRequestDto request) {
        boolean isUpdated = folderService.updateFolderTitle(request.getFolderId(), request.getNewTitle());
        if (isUpdated) {
            return ResponseEntity.ok("폴더 이름 수정 완료");
        } else {
            return ResponseEntity.badRequest().body("폴더 이름 수정 실패");
        }
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable("folderId") UUID folderId) {
        boolean isDeleted = folderService.deleteFolder(folderId);
        if (isDeleted) {
            return ResponseEntity.ok("폴더 삭제 완료");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
