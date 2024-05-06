package com.sixb.stab.auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenResponseDto {

	private String tokenType;
	private String accessToken;
	private String refreshToken;

}
