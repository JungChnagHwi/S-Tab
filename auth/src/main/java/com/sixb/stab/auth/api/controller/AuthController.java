package com.sixb.stab.auth.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sixb.stab.auth.api.service.AuthService;
import com.sixb.stab.auth.dto.request.LoginRequestDto;
import com.sixb.stab.auth.dto.request.LogoutRequestDto;
import com.sixb.stab.auth.dto.response.TokenResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
		} catch (JsonProcessingException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 ID토큰입니다");
		}
	}

}
