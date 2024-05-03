package com.sixb.stab.auth.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisInfo {

	private int maxRedirects;
	private String password;
	private String connectIp;
	private List<String> nodes;

}
