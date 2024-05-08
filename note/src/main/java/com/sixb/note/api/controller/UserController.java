package com.sixb.note.api.controller;

import com.sixb.note.api.service.UserService;
import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.NicknameResponseDto;
import com.sixb.note.dto.response.UserInfoResponseDto;
import com.sixb.note.entity.User;
import com.sixb.note.exception.ExistUserException;
import com.sixb.note.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserInfo(@RequestParam("userId") long userId) {
		try {
			UserInfoResponseDto response = userService.getUserInfo(userId);
			return ResponseEntity.ok(response);
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(e.getMessage());
		}
    }

	@PostMapping
	public ResponseEntity<?> signup(@RequestParam("userId") long userId,
									@RequestBody UserInfoRequestDto request) {
		try {
			UserInfoResponseDto response = userService.signup(userId, request);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (ExistUserException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PatchMapping
	public ResponseEntity<?> updateUserInfo(@RequestParam("userId") long userId,
											@RequestBody UserInfoRequestDto request) {
		try {
			UserInfoResponseDto response = userService.updateUserInfo(userId, request);
			return ResponseEntity.ok(response);
		} catch (UserNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/{nickname}")
	public ResponseEntity<NicknameResponseDto> checkNickname(@PathVariable String nickname) {
		NicknameResponseDto response = userService.checkNickname(nickname);
		return ResponseEntity.ok(response);
	}

}
