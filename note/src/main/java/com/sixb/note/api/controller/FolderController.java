package com.sixb.note.api.controller;

import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.CreateFolderRequestDto;
import com.sixb.note.dto.folder.CreateFolderResponseDto;
import com.sixb.note.dto.folder.FolderResponseDto;
import com.sixb.note.dto.folder.UpdateFolderTitleRequestDto;
import com.sixb.note.exception.NotFoundException;
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
    public ResponseEntity<FolderResponseDto> getFolderById(@PathVariable("folderId") String folderId) {
        FolderResponseDto folderInfo = folderService.getFolderDetail(folderId);
        return ResponseEntity.ok(folderInfo);
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<FolderResponseDto> getSpaceById(@PathVariable("spaceId") String spaceId) {
        FolderResponseDto spaceInfo = folderService.getSpaceDetail(spaceId);
        return ResponseEntity.ok(spaceInfo);
    }

    @PostMapping
    public ResponseEntity<CreateFolderResponseDto> createFolder(@RequestBody CreateFolderRequestDto request) {
        CreateFolderResponseDto createdFolder = folderService.createFolder(request);
        return ResponseEntity.ok(createdFolder);
    }

    @PatchMapping("/rename")
    public ResponseEntity<String> updateFolderTitle(@RequestBody UpdateFolderTitleRequestDto request) {
        try {
            folderService.updateFolderTitle(request.getFolderId(), request.getNewTitle());
            return ResponseEntity.ok("폴더 이름 수정 완료");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{folderId}")
    public ResponseEntity<String> deleteFolder(@PathVariable("folderId") String folderId) {
        boolean isUpdated = folderService.deleteFolder(folderId);
        if (isUpdated) {
            return ResponseEntity.ok("폴더 삭제 완료");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
