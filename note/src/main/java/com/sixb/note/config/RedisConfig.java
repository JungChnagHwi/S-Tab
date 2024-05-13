package com.sixb.note.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sixb.note.util.RedisInfo;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.sixb.note.util.Const.PAGE;
import static com.sixb.note.util.Const.PAGE_CACHE_EXPIRE_TIME;

@Configuration
@EnableCaching
@EnableRedisRepositories
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

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper mapper = new ObjectMapper();

		mapper.activateDefaultTyping(
				BasicPolymorphicTypeValidator
						.builder()
						.allowIfSubType(Object.class)
						.build(),
				ObjectMapper.DefaultTyping.NON_FINAL
		);
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.disableCachingNullValues()
				.serializeKeysWith(
						RedisSerializationContext.SerializationPair
								.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair
						.fromSerializer(new GenericJackson2JsonRedisSerializer(mapper)))
				.entryTtl(PAGE_CACHE_EXPIRE_TIME);
		Map<String, RedisCacheConfiguration> configurations = new HashMap<>();

		configurations.put(PAGE, cacheConfiguration);

		return RedisCacheManager.builder(redisConnectionFactory)
				.withInitialCacheConfigurations(configurations)
				.build();
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		return redisTemplate;
	}

}
