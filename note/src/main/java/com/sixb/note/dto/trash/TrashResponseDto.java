package com.sixb.note.dto.trash;

import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrashResponseDto {

	private List<Folder> folders;
	private List<Note> notes;
	private List<Page> pages;

}
