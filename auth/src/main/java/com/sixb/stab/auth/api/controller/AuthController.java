package com.sixb.stab.auth.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sixb.stab.auth.api.service.AuthService;
import com.sixb.stab.auth.dto.request.LoginRequestDto;
import com.sixb.stab.auth.dto.request.LogoutRequestDto;
import com.sixb.stab.auth.dto.response.TokenResponseDto;
import com.sixb.stab.auth.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDto request) {
		try {
			TokenResponseDto response = authService.login(request);
			return ResponseEntity.ok(response);
		} catch (InvalidTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody LogoutRequestDto request) {
		authService.logout(request);
		return ResponseEntity.ok("로그아웃 완료했습니다.");
	}

	@GetMapping("/reissue")
	public ResponseEntity<?> reissue(@RequestHeader("Authorization") String token) {
		try {
			String refreshToken = token.substring(7);
			TokenResponseDto response = authService.reissue(refreshToken);
			return ResponseEntity.ok(response);
		} catch (InvalidTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		}
	}

}
