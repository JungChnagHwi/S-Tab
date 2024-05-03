package com.sixb.stab.auth.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixb.stab.auth.dto.PayloadDto;
import com.sixb.stab.auth.dto.request.LoginRequestDto;
import com.sixb.stab.auth.dto.request.LogoutRequestDto;
import com.sixb.stab.auth.dto.response.TokenResponseDto;
import com.sixb.stab.auth.entity.BlackList;
import com.sixb.stab.auth.entity.RefreshToken;
import com.sixb.stab.auth.exception.InvalidTokenException;
import com.sixb.stab.auth.jwt.JwtTokenProvider;
import com.sixb.stab.auth.repository.BlackListRepository;
import com.sixb.stab.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

	private static final String BEARER_TYPE = "Bearer";

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final BlackListRepository blackListRepository;


	public TokenResponseDto login(LoginRequestDto request) throws JsonProcessingException {
		String idToken = request.getIdToken();
		long userId = getUserId(idToken);

		Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUserId(userId);

		if (refreshTokenOptional.isPresent()) {
			RefreshToken refreshToken = refreshTokenOptional.get();
			String token = refreshToken.getRefreshToken();

			BlackList blackList = BlackList.builder()
					.token(token)
					.expiration(jwtTokenProvider.getExpiration(token))
					.build();

			blackListRepository.save(blackList);
			refreshTokenRepository.delete(refreshToken);
		}

		return createToken(userId);
	}

	private TokenResponseDto createToken(long userId) {
		String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(userId));
		String refreshToken = jwtTokenProvider.createRefreshToken();

		RefreshToken token = RefreshToken.builder()
				.userId(userId)
				.refreshToken(refreshToken)
				.build();

		refreshTokenRepository.save(token);

		return TokenResponseDto.builder()
				.tokenType(BEARER_TYPE)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	private long getUserId(String idToken) throws JsonProcessingException {
		String[] splitToken = idToken.split("\\.");
		String base64EncodedPayload = splitToken[1];
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String payload = new String(decoder.decode(base64EncodedPayload));
		PayloadDto payloadDto = new ObjectMapper().readValue(payload, PayloadDto.class);
		return payloadDto.getSub();
	}

	public void logout(LogoutRequestDto request) {
		String accessToken = request.getAccessToken();
		String refreshToken = request.getRefreshToken();

		refreshTokenRepository.deleteById(refreshToken);

		if (jwtTokenProvider.isValid(accessToken)) {
			BlackList access = BlackList.builder()
					.token(accessToken)
					.expiration(jwtTokenProvider.getExpiration(accessToken))
					.build();
			blackListRepository.save(access);
		}

		if (jwtTokenProvider.isValid(refreshToken)) {
			BlackList refresh = BlackList.builder()
					.token(refreshToken)
					.expiration(jwtTokenProvider.getExpiration(refreshToken))
					.build();
			blackListRepository.save(refresh);
		}
	}

	public TokenResponseDto reissue(String refreshToken) throws InvalidTokenException {
		RefreshToken token = refreshTokenRepository.findById(refreshToken)
				.orElseThrow(() -> new InvalidTokenException("유효하지 않은 토큰입니다."));

		if (!jwtTokenProvider.isValid(token.getRefreshToken())) {
			throw new InvalidTokenException("유효하지 않은 토큰입니다.");
		}

		long userId  = token.getUserId();

		refreshTokenRepository.deleteById(refreshToken);

		BlackList blackList = BlackList.builder()
				.token(refreshToken)
				.expiration(jwtTokenProvider.getExpiration(refreshToken))
				.build();

		blackListRepository.save(blackList);

		return createToken(userId);
	}

}
