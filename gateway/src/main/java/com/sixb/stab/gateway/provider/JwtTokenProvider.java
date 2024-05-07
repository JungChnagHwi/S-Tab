package com.sixb.stab.gateway.provider;

import com.sixb.stab.gateway.repository.BlackListRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Component
public class JwtTokenProvider {

	private final Key key;

	private final BlackListRepository blackListRepository;

	public JwtTokenProvider(@Value("${jwt.token.secret-key}") String secretKey,
							BlackListRepository blackListRepository) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.blackListRepository = blackListRepository;
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

	public String getUserId(String token) {
		return  Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}

}
