package com.sixb.note.api.service;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.NicknameResponseDto;
import com.sixb.note.dto.response.UserInfoResponseDto;
import com.sixb.note.exception.UserNotFoundException;
import com.sixb.note.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sixb.note.entity.User;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfoResponseDto getUserInfo(long userId) throws UserNotFoundException {
        return userRepository.getUserInfo(userId)
                .orElseThrow(() -> new UserNotFoundException("회원가입이 필요합니다."));
    }

    public UserInfoResponseDto signup(long userId, UserInfoRequestDto request) {
        return userRepository.signup(userId, request);
    }

    public UserInfoResponseDto updateUserInfo(long userId, UserInfoRequestDto request) throws UserNotFoundException {
        return userRepository.updateUserInfo(userId, request)
                .orElseThrow(() -> new UserNotFoundException("잘못된 유저입니다."));
    }

    public NicknameResponseDto checkNickname(String nickname) {
        return userRepository.findNicknameCount(nickname);
    }

    public List<User> findUsersBySpaceId(UUID spaceId) {
        return userRepository.findUsersBySpaceId(spaceId);
    }

}
