package com.sixb.note.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelocateFolderRequestDto {

	private String folderId;
	private String parentFolderId;

}
