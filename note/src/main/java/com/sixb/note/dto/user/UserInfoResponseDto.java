package com.sixb.note.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInfoResponseDto {

	private String nickname;
	private String profileImg;
	private String rootFolderId;

}
