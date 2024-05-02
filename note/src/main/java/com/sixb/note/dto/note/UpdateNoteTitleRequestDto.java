package com.sixb.note.dto.note;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class UpdateNoteTitleRequestDto {
    private UUID noteId;
    private String newTitle;
}
