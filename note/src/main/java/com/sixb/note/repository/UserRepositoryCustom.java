package com.sixb.note.repository;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.NicknameResponseDto;
import com.sixb.note.dto.response.UserInfoResponseDto;

import java.util.*;

public interface UserRepositoryCustom {

	Optional<UserInfoResponseDto> getUserInfo(long userId);

	boolean isSignedUpUser(long userId);

	UserInfoResponseDto signup(long userId, UserInfoRequestDto request);

	Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request);

	NicknameResponseDto findNicknameCount(String nickname);

}
