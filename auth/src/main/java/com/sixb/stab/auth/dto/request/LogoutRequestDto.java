package com.sixb.stab.auth.dto.request;

import lombok.Data;

@Data
public class LogoutRequestDto {

	private String accessToken;
	private String refreshToken;

}
