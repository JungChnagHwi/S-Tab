package com.sixb.note.api.controller;

import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.CreateFolderRequestDto;
import com.sixb.note.dto.folder.CreateFolderResponseDto;
import com.sixb.note.dto.folder.FolderResponseDto;
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
}
