package com.sixb.note.api.service;

import com.sixb.note.dto.user.UserInfoRequestDto;
import com.sixb.note.dto.user.NicknameResponseDto;
import com.sixb.note.dto.user.UserInfoResponseDto;
import com.sixb.note.exception.ExistUserException;
import com.sixb.note.exception.UserNotFoundException;
import com.sixb.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserInfoResponseDto getUserInfo(long userId) throws UserNotFoundException {
		return userRepository.getUserInfo(userId)
				.orElseThrow(() -> new UserNotFoundException("회원가입이 필요합니다."));
	}

	public UserInfoResponseDto signup(long userId, UserInfoRequestDto request) throws ExistUserException {
		if (userRepository.isSignedUpUser(userId)) {
			throw new ExistUserException("이미 회원가입이 된 유저입니다.");
		}

		return userRepository.signup(userId, request);
	}

	public UserInfoResponseDto updateUserInfo(long userId, UserInfoRequestDto request) throws UserNotFoundException {
		return userRepository.updateUserInfo(userId, request)
				.orElseThrow(() -> new UserNotFoundException("잘못된 유저입니다."));
	}

	public NicknameResponseDto checkNickname(String nickname) {
		return userRepository.findNicknameCount(nickname);
	}

}
