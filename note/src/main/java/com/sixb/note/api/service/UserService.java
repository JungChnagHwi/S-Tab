package com.sixb.note.api.service;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.NicknameResponseDto;
import com.sixb.note.dto.response.UserInfoResponseDto;
import com.sixb.note.exception.InvalidTokenException;
import com.sixb.note.exception.UserNotFoundException;
import com.sixb.note.jwt.JwtTokenProvider;
import com.sixb.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sixb.note.entity.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    public UserInfoResponseDto getUserInfo(String token) throws InvalidTokenException, UserNotFoundException {
        long userId = jwtTokenProvider.getUserId(token);
        return userRepository.getUserInfo(userId)
                .orElseThrow(() -> new UserNotFoundException("회원가입이 필요합니다."));
    }

    public UserInfoResponseDto signup(String token, UserInfoRequestDto request) throws InvalidTokenException {
        long userId = jwtTokenProvider.getUserId(token);
        return userRepository.signup(userId, request);
    }

    public UserInfoResponseDto updateUserInfo(String token, UserInfoRequestDto request) throws InvalidTokenException, UserNotFoundException {
        long userId = jwtTokenProvider.getUserId(token);
        return userRepository.updateUserInfo(userId, request)
                .orElseThrow(() -> new UserNotFoundException("잘못된 유저입니다."));
    }

    public NicknameResponseDto checkNickname(String nickname) {
        return userRepository.findNicknameCount(nickname);
    }

    public List<User> findUsersBySpaceId(String spaceId) {
        return userRepository.findUsersBySpaceId(spaceId);
    }


    public User getUserDetails(String userId) {
        return userRepository.findUserById(userId);
    }

}
