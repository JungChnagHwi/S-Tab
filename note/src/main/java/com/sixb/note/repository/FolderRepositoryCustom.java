package com.sixb.note.repository;

import com.sixb.note.dto.folder.FolderResponseDto;

public interface FolderRepositoryCustom {

	FolderResponseDto getFolderByName(long userId, String name, String spaceId);

	void deleteFolder(String folderId);

}
