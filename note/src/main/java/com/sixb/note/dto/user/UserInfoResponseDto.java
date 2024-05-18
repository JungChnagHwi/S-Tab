package com.sixb.note.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {

	private String nickname;
	private String profileImg;
	private String privateSpaceId;
	private String rootFolderId;

}
