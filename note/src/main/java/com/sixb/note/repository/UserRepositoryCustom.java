package com.sixb.note.repository;

import com.sixb.note.dto.response.UserInfoResponseDto;

import java.util.*;

public interface UserRepositoryCustom {

	Optional<UserInfoResponseDto> getUserInfo(long userId);

}
