package com.sixb.note.api.controller;

import com.sixb.note.api.service.TrashService;
import com.sixb.note.dto.Trash.TrashRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trash")
public class TrashController {
    @Autowired
    private TrashService trashService;
    @GetMapping
    public ResponseEntity<List<Object>> getDeletedItems(long userId) {
        List<Object> deletedItems = trashService.findDeletedItems(userId);
        return ResponseEntity.ok(deletedItems);
    }

    @PatchMapping
    public ResponseEntity<String> recoverItem(@RequestBody TrashRequestDto trashRequestDto) {
        boolean isRecovered = trashService.recoverItem(trashRequestDto);
        if (isRecovered) {
            return ResponseEntity.ok("복원 완료");
        } else {
            return ResponseEntity.badRequest().body("복원 실패");
        }
    }
}
