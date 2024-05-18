package com.sixb.note.dto.folder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateFolderResponseDto {
	private String folderId;
	private String title;
	@JsonProperty(value = "isLiked")
	private boolean isLiked;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@JsonProperty(value = "isDeleted")
	private boolean isDeleted;
}
