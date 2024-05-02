package com.sixb.stab.auth.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {

	private String idToken;

}
