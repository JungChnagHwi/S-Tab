package com.sixb.note.dto.space;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceResponseDto {
	private String spaceId;
	private String rootFolderId;
	private String title;
	@Builder.Default
	private Boolean isPublic = false;
	private String spaceMd;
	private List<UserResponse> users;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserResponse {
		private String nickname;
		private String profileImg;
	}
}
