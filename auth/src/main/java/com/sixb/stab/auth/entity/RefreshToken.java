package com.sixb.stab.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@Builder
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 400)
public class RefreshToken {

	@Indexed
	private long userId;

	@Id
	private String refreshToken;

}
