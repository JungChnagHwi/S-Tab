package com.sixb.note.api.service;

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

    public List<User> findUsersBySpaceId(UUID spaceId) {
        return userRepository.findUsersBySpaceId(spaceId);
    }
}
