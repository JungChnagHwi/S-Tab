package com.sixb.note.api.controller;

import com.sixb.note.api.service.FolderService;
import com.sixb.note.dto.folder.FolderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
