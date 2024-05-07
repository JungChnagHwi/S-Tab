package com.sixb.note.api.controller;

import com.sixb.note.api.service.SpaceService;
import com.sixb.note.dto.folder.UpdateFolderTitleRequestDto;
import com.sixb.note.dto.space.SpaceRequestDto;
import com.sixb.note.dto.space.SpaceResponseDto;
import com.sixb.note.dto.space.UpdateSpaceTitleRequestDto;
import com.sixb.note.entity.Space;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/space")
public class SpaceController {
    @Autowired
    private SpaceService spaceService;

    @GetMapping("/list")
    public ResponseEntity<List<SpaceResponseDto>> getAllSpaceDetails() {
        List<SpaceResponseDto> spaces = spaceService.findAllSpaceDetails();
        return ResponseEntity.ok(spaces);
    }

    @PostMapping
    public ResponseEntity<SpaceResponseDto> createSpace(@RequestBody SpaceRequestDto requestDto) {
        SpaceResponseDto createdSpace = spaceService.createSpace(requestDto);
        return new ResponseEntity<>(createdSpace, HttpStatus.CREATED);
    }

    @PatchMapping("/rename")
    public ResponseEntity<String> updateSpaceTitle(@RequestBody UpdateSpaceTitleRequestDto request) {
        boolean isUpdated = spaceService.updateSpaceTitle(request.getSpaceId(), request.getNewTitle());
        if (isUpdated) {
            return ResponseEntity.ok("스페이스 이름 수정 완료");
        } else {
            return ResponseEntity.badRequest().body("스페이스 이름 수정 실패");
        }
    }

    @DeleteMapping("/{spaceId}")
    public ResponseEntity<String> deleteSpace(@PathVariable String spaceId) {
        boolean isUpdated = spaceService.deleteSpace(spaceId);
        if (isUpdated) {
            return ResponseEntity.ok("Space 삭제 완료");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
