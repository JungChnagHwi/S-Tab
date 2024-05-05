package com.sixb.stab.gateway.filter;

import com.sixb.stab.gateway.provider.JwtTokenProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class TokenValidationCheckFilter extends AbstractGatewayFilterFactory<TokenValidationCheckFilter.Config> {

	private final JwtTokenProvider jwtTokenProvider;

	public TokenValidationCheckFilter(JwtTokenProvider jwtTokenProvider) {
		super(Config.class);
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			ServerHttpResponse response = exchange.getResponse();

			if (request.getHeaders().containsKey("Authorization")) {
				String token = Objects.requireNonNull(request.getHeaders().getFirst("Authorization"));

				if (!token.startsWith("Bearer ")) {
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					DataBuffer buffer = response.bufferFactory().wrap("토큰이 존재하지 않습니다.".getBytes(StandardCharsets.UTF_8));
					return response.writeWith(Mono.just(buffer));
				}

				token = token.substring(7);

				if (!jwtTokenProvider.validateToken(token)) {
					response.setStatusCode(HttpStatus.UNAUTHORIZED);
					DataBuffer buffer = response.bufferFactory().wrap("토큰이 유효하지 않습니다.".getBytes(StandardCharsets.UTF_8));
					return response.writeWith(Mono.just(buffer));
				}

				String userId = jwtTokenProvider.getUserId(token);

				ServerHttpRequest modifiedRequest =
						request.mutate()
								.uri(UriComponentsBuilder.fromUri(request.getURI())
										.queryParam("userId", userId)
										.build()
										.toUri())
								.build();

				ServerWebExchange modifiedExchange =
						exchange.mutate().request(modifiedRequest).build();

				return chain.filter(modifiedExchange);
			} else {
				response.setStatusCode(HttpStatus.UNAUTHORIZED);
				DataBuffer buffer = response.bufferFactory().wrap("토큰이 존재하지 않습니다.".getBytes(StandardCharsets.UTF_8));
				return response.writeWith(Mono.just(buffer));
			}
		};
	}

	public static class Config {
	}

}
