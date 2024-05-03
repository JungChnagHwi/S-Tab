package com.sixb.stab.auth.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@RedisHash(value = "blackList")
public class BlackList {

	@TimeToLive
	private long expiration;

	@Id
	private String token;

}
