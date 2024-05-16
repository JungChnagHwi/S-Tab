package com.sixb.note.config;

import com.sixb.note.dto.page.PageInfoDto;
import com.sixb.note.listener.RedisDataListener;
import com.sixb.note.util.RedisInfo;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.sixb.note.util.Const.PAGE;
import static com.sixb.note.util.Const.PAGE_CACHE_EXPIRE_TIME;

@Configuration
//@EnableCaching
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

//	@Bean
//	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//		RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//				.disableCachingNullValues()
//				.serializeKeysWith(
//						RedisSerializationContext.SerializationPair
//								.fromSerializer(new StringRedisSerializer()))
//				.serializeValuesWith(
//						RedisSerializationContext.SerializationPair
//								.fromSerializer(new Jackson2JsonRedisSerializer<>(PageInfoDto.class)))
//				.entryTtl(PAGE_CACHE_EXPIRE_TIME);
//
//		Map<String, RedisCacheConfiguration> configurations = new HashMap<>();
//		configurations.put(PAGE, cacheConfiguration.entryTtl(PAGE_CACHE_EXPIRE_TIME));
//
//		return RedisCacheManager.RedisCacheManagerBuilder
//				.fromConnectionFactory(redisConnectionFactory)
//				.cacheDefaults(cacheConfiguration)
//				.withInitialCacheConfigurations(configurations)
//				.build();
//	}

	@Bean
	public RedisTemplate<String, PageInfoDto> redisTemplate() {
		RedisTemplate<String, PageInfoDto> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(PageInfoDto.class));
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}

	@Bean
	RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
											MessageListenerAdapter listenerAdapter) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic("__keyevent@*__:expired"));
		return container;
	}

	@Bean
	MessageListenerAdapter listenerAdapter(RedisDataListener listener) {
		return new MessageListenerAdapter(listener, "messageReceived");
	}

}
