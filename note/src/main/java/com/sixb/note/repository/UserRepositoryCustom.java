package com.sixb.note.repository;

import com.sixb.note.dto.user.NicknameResponseDto;
import com.sixb.note.dto.user.UserInfoRequestDto;
import com.sixb.note.dto.user.UserInfoResponseDto;

import java.util.*;

public interface UserRepositoryCustom {

	Optional<UserInfoResponseDto> getUserInfo(long userId);

	boolean isSignedUpUser(long userId);

	UserInfoResponseDto signup(long userId, UserInfoRequestDto request);

	Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request);

	NicknameResponseDto findNicknameCount(String nickname);

}
