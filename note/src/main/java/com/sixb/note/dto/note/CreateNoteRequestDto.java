package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateNoteRequestDto {
    private UUID parentFolderId;
    private String title;
    private int color;
    private int template;
    private int direction;
}
