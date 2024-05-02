package com.sixb.stab.auth.jwt;

import com.sixb.stab.auth.entity.BlackList;
import com.sixb.stab.auth.repository.BlackListRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

	private final Key key;
	private final long accessTokenValidity;
	private final long refreshTokenValidity;

	private final BlackListRepository blackListRepository;

	@Autowired
	public JwtTokenProvider(
			@Value("${jwt.token.secret-key}") String secretKey,
			@Value("${jwt.access-token.expire-length}") long accessTokenValidity,
			@Value("${jwt.refresh-token.expire-length}") long refreshTokenValidity,
			BlackListRepository blackListRepository) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.accessTokenValidity = accessTokenValidity;
		this.refreshTokenValidity = refreshTokenValidity;
		this.blackListRepository = blackListRepository;
	}

	public String createAccessToken(String payload) {
		return createToken(payload, accessTokenValidity);
	}

	public String createRefreshToken() {
		byte[] array = new byte[7];
		new Random().nextBytes(array);
		String generatedString = new String(array, StandardCharsets.UTF_8);
		return createToken(generatedString, refreshTokenValidity);
	}

	private String createToken(String payload, long expireLength) {
		Claims claims = Jwts.claims().setSubject(payload);
		Date now = new Date();
		Date validity = new Date(now.getTime() + expireLength);
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(now)
				.setExpiration(validity)
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public long getExpiration(String token) {
		Jws<Claims> claimsJws = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
		Date expiration = claimsJws.getBody().getExpiration();
		return (expiration.getTime() - new Date().getTime()) / 1000L;
	}

	public boolean isValid(String token) {
		if (blackListRepository.findById(token).isPresent()) {
			return false;
		}

		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token);
			return !claimsJws.getBody().getExpiration().before(new Date());
		} catch (JwtException | IllegalArgumentException exception) {
			return false;
		}
	}

}
