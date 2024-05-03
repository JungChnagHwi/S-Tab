package com.sixb.stab.auth.dto;

import lombok.Data;

@Data
public class PayloadDto {

	private String aud;
	private long sub;
	private long auth_time;
	private String iss;
	private long exp;
	private long iat;

}
