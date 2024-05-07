package com.sixb.note.dto.space;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSpaceTitleRequestDto {
    private String spaceId;
    private String newTitle;
}
