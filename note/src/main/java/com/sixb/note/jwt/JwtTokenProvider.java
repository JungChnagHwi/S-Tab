package com.sixb.note.jwt;

import com.sixb.note.exception.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtTokenProvider {

	private final Key key;

	public JwtTokenProvider(@Value("${jwt.token.secret-key}") String secretKey) {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public long getUserId(String token) throws InvalidTokenException {
		try {
			token = token.substring(7);
			String subject =  Jwts.parserBuilder()
					.setSigningKey(key)
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
			return Long.parseLong(subject);
		} catch (ExpiredJwtException e) {
			throw new InvalidTokenException("만료된 토큰입니다.");
		} catch (JwtException e) {
			throw new InvalidTokenException("유효하지 않은 토큰 입니다");
		}
	}

}
