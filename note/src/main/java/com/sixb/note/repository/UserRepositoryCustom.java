package com.sixb.note.repository;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.UserInfoResponseDto;

import java.util.*;

public interface UserRepositoryCustom {

	Optional<UserInfoResponseDto> getUserInfo(long userId);

	Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request);

}
