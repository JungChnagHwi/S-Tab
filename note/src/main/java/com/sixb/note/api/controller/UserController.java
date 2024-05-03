package com.sixb.note.api.controller;

import com.sixb.note.api.service.UserService;
import com.sixb.note.dto.response.UserInfoResponseDto;
import com.sixb.note.exception.InvalidTokenException;
import com.sixb.note.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
		try {
			UserInfoResponseDto response = userService.getUserInfo(token);
			return ResponseEntity.ok(response);
		} catch (InvalidTokenException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		} catch (UserNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
    }

}
