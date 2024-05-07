package com.sixb.stab.gateway.redis;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisInfo redisInfo;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisClusterConfiguration redisClusterConfiguration =
				new RedisClusterConfiguration(redisInfo.getNodes());
		redisClusterConfiguration.setPassword(redisInfo.getPassword());
		redisClusterConfiguration.setMaxRedirects(redisInfo.getMaxRedirects());

		ClusterTopologyRefreshOptions clusterTopologyRefreshOptions =
				ClusterTopologyRefreshOptions.builder()
						.enableAllAdaptiveRefreshTriggers()
						.enablePeriodicRefresh(Duration.ofHours(1L))
						.build();

		ClientOptions clientOptions = ClusterClientOptions.builder()
				.topologyRefreshOptions(clusterTopologyRefreshOptions)
				.build();

		LettuceClientConfiguration clientConfiguration =
				LettuceClientConfiguration.builder()
						.commandTimeout(Duration.of(10, ChronoUnit.SECONDS))
						.clientOptions(clientOptions)
						.readFrom(ReadFrom.REPLICA_PREFERRED)
						.build();

		return new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);
	}

}
