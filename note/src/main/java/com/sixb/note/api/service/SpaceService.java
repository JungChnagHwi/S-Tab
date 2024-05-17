package com.sixb.note.api.service;

import com.sixb.note.dto.space.SpaceMdRequestDto;
import com.sixb.note.dto.space.SpaceMdResponseDto;
import com.sixb.note.dto.space.SpaceRequestDto;
import com.sixb.note.dto.space.SpaceResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Space;
import com.sixb.note.entity.User;
import com.sixb.note.exception.ExistUserException;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.exception.SpaceNotFoundException;
import com.sixb.note.exception.UserNotFoundException;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.repository.UserRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceService {

	private final SpaceRepository spaceRepository;
	private final UserRepository userRepository;

	public List<SpaceResponseDto> findAllSpaceDetails(long userId) throws UserNotFoundException {
		User users = userRepository.findUserById(userId)
				.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
		List<Space> spaces = spaceRepository.findSpaces(users.getUserId());

		return spaces.stream().map(space -> {
			SpaceResponseDto dto = new SpaceResponseDto();
			dto.setSpaceId(space.getSpaceId());
			dto.setRootFolderId(space.getRootFolderId());
			dto.setTitle(space.getTitle());
			dto.setIsPublic(space.getIsPublic());
			dto.setCreatedAt(space.getCreatedAt());
			dto.setUpdatedAt(space.getUpdatedAt());

			List<User> user = userRepository.findUsersBySpaceId(space.getSpaceId());
			List<SpaceResponseDto.UserResponse> userResponses = user.stream()
					.map(spaceUser -> {
						SpaceResponseDto.UserResponse userResponse = new SpaceResponseDto.UserResponse();
						userResponse.setNickname(spaceUser.getNickname());
						userResponse.setProfileImg(spaceUser.getProfileImg());
						return userResponse;
					}).collect(Collectors.toList());

			dto.setUsers(userResponses);
			return dto;
		}).collect(Collectors.toList());
	}

	public SpaceResponseDto findSpaceDetails(long userId, String spaceId) throws UserNotFoundException {
		User userInfo = userRepository.findUserById(userId)
				.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));
		Space space = spaceRepository.findSpaceByIdAndUserId(spaceId, userInfo.getUserId());


		SpaceResponseDto dto = new SpaceResponseDto();
		dto.setSpaceId(space.getSpaceId());
		dto.setTitle(space.getTitle());
		dto.setIsPublic(space.getIsPublic());
		dto.setCreatedAt(space.getCreatedAt());
		dto.setUpdatedAt(space.getUpdatedAt());

		List<User> user = userRepository.findUsersBySpaceId(space.getSpaceId());
		List<SpaceResponseDto.UserResponse> userResponses = user.stream()
				.map(spaceUser -> {
					SpaceResponseDto.UserResponse userResponse = new SpaceResponseDto.UserResponse();
					userResponse.setNickname(spaceUser.getNickname());
					userResponse.setProfileImg(spaceUser.getProfileImg());
					return userResponse;
				}).collect(Collectors.toList());

		dto.setUsers(userResponses);
		return dto;
	}


	public SpaceResponseDto createSpace(SpaceRequestDto requestDto, long userId) throws UserNotFoundException {
		User user = userRepository.findUserById(userId)
				.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

		LocalDateTime now = LocalDateTime.now();
		String folderId = IdCreator.create("f");
		String spaceId = IdCreator.create("s");

		Space newSpace = Space.builder()
				.spaceId(spaceId)
				.rootFolderId(folderId)
				.isPublic(true)
				.spaceMd("")
				.createdAt(now)
				.updatedAt(now)
				.build();

		Folder newFolder = Folder.builder()
				.folderId(folderId)
				.spaceId(spaceId)
				.title("root")
				.createdAt(now)
				.updatedAt(now)
				.build();

		newSpace.setFolder(newFolder);
		newSpace.setUsers(List.of(user));
		spaceRepository.save(newSpace);

		return SpaceResponseDto.builder()
				.spaceId(newSpace.getSpaceId())
				.rootFolderId(folderId)
				.title(newSpace.getTitle())
				.isPublic(newSpace.getIsPublic())
				.spaceMd(newSpace.getSpaceMd())
				.createdAt(newSpace.getCreatedAt())
				.updatedAt(newSpace.getUpdatedAt())
				.users(newSpace.getUsers().stream()
						.map(u -> SpaceResponseDto.UserResponse.builder()
								.nickname(u.getNickname())
								.profileImg(u.getProfileImg())
								.build())
						.toList())
				.build();
	}

	//스페이스 이름 변경
	public void updateSpaceTitle(String spaceId, String newTitle) throws NotFoundException {
		Space space = spaceRepository.findSpaceById(spaceId);
		if (space == null) {
			throw new NotFoundException("스페이스 이름 수정 실패");
		}
		spaceRepository.updateSpaceTitle(spaceId, newTitle);
	}

	//스페이스 참가
	public void joinSpace(long userId, String spaceId) throws ExistUserException, SpaceNotFoundException, UserNotFoundException {
		if (spaceRepository.isJoinedUser(userId, spaceId)) {
			throw new ExistUserException("이미 가입된 유저입니다.");
		}

		User user = userRepository.findUserById(userId)
				.orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다."));

		Space space = spaceRepository.findSpaceById(spaceId);

		if (space == null) {
			throw new SpaceNotFoundException("존재하지 않는 스페이스입니다.");
		}

		user.getSpaces().add(space);
		userRepository.save(user);
	}


	public SpaceMdResponseDto findSpaceMarkdown(String spaceId) throws NotFoundException {
		Space space = spaceRepository.findSpaceById(spaceId);
		if (space == null) {
			throw new NotFoundException("스페이스를 찾을 수 없습니다.");
		}
		SpaceMdResponseDto dto = new SpaceMdResponseDto();
		dto.setData(space.getSpaceMd());
		return dto;
	}

	public void updateSpaceMarkdown(SpaceMdRequestDto requestDto) throws NotFoundException {
		Space space = spaceRepository.findSpaceById(requestDto.getSpaceId());
		if (space == null) {
			throw new NotFoundException("스페이스를 찾을 수 없습니다.");
		}
		space.setSpaceMd(requestDto.getData());
		spaceRepository.save(space);
	}

	// 스페이스 탈퇴
	public void leaveSpace(long userId, String spaceId) throws SpaceNotFoundException {
		Space space = spaceRepository.findSpaceById(spaceId);

		if (space == null) {
			throw new SpaceNotFoundException("존재하지 않는 스페이스입니다.");
		}

		if (!spaceRepository.isJoinedUser(userId, spaceId)) {
			throw new SpaceNotFoundException("스페이스에 가입되지 않았습니다.");
		}

		if (!spaceRepository.isPublicSpace(spaceId)) {
			throw new SpaceNotFoundException("개인 스페이스는 탈퇴할 수 없습니다.");
		}

		spaceRepository.removeUserFromSpace(userId, spaceId);

		List<User> remainingUsers = userRepository.findUsersBySpaceId(spaceId);
		if (remainingUsers.isEmpty()) {
			spaceRepository.deleteSpace(spaceId);
		}
	}

}
