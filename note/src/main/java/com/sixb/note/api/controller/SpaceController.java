package com.sixb.note.api.controller;

import com.sixb.note.api.service.SpaceService;
import com.sixb.note.dto.space.*;
import com.sixb.note.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/space")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;

    @GetMapping("/list")
    public ResponseEntity<List<SpaceResponseDto>> getAllSpaceDetails(long userId) {
        List<SpaceResponseDto> spaces = spaceService.findAllSpaceDetails(userId);
        return ResponseEntity.ok(spaces);
    }

    @GetMapping("/{spaceId}")
    public ResponseEntity<SpaceResponseDto> getSpaceDetails(long userId, @PathVariable String spaceId) {
        try {
            SpaceResponseDto spaceDetails = spaceService.findSpaceDetails(userId, spaceId);
            return ResponseEntity.ok(spaceDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SpaceResponseDto> createSpace(@RequestBody SpaceRequestDto requestDto, long userId) {
        SpaceResponseDto createdSpace = spaceService.createSpace(requestDto, userId);
        return new ResponseEntity<>(createdSpace, HttpStatus.CREATED);
    }

    @PatchMapping("/rename")
    public ResponseEntity<String> updateSpaceTitle(@RequestBody UpdateSpaceTitleRequestDto request) {
        try {
            spaceService.updateSpaceTitle(request.getSpaceId(), request.getNewTitle());
            return ResponseEntity.ok("스페이스 이름 수정 완료");
        } catch (NotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @DeleteMapping("/{spaceId}")
//    public ResponseEntity<String> deleteSpace(@PathVariable String spaceId) {
//        boolean isUpdated = spaceService.deleteSpace(spaceId);
//        if (isUpdated) {
//            return ResponseEntity.ok("Space 삭제 완료");
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

    @PostMapping("/join")
    public ResponseEntity<String> joinSpace(long userId, @RequestBody JoinSpaceRequestDto joinSpaceRequestDto) {
        spaceService.joinSpace(userId, joinSpaceRequestDto.getSpaceId());
        return ResponseEntity.ok("스페이스 참여 성공");
    }

    @GetMapping("/cover/{spaceId}")
    public ResponseEntity<SpaceMdResponseDto> getSpaceMarkdown(@PathVariable String spaceId) {
        try {
            SpaceMdResponseDto responseDto = spaceService.findSpaceMarkdown(spaceId);
            return ResponseEntity.ok(responseDto);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/cover")
    public ResponseEntity<String> updateSpaceMarkdown(@RequestBody SpaceMdRequestDto requestDto) {
        try {
            spaceService.updateSpaceMarkdown(requestDto);
            return ResponseEntity.ok("스페이스 표지 수정 성공");
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{spaceId}")
    public ResponseEntity<String> leaveSpace(long userId, @PathVariable String spaceId) {
        try {
            spaceService.leaveSpace(userId, spaceId);
            return ResponseEntity.ok("스페이스에서 성공적으로 탈퇴하였습니다.");
        } catch (NotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
