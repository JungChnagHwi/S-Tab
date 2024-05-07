package com.sixb.note.dto.Like;

import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LikeResponseDto {
    private List<Folder> folders;
    private List<Note> notes;
    private List<Page> pages;

    public LikeResponseDto(List<Folder> folders, List<Note> notes, List<Page> pages) {
        this.folders = folders;
        this.notes = notes;
        this.pages = pages;
    }

}
