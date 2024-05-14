package com.sixb.note.api.service;

import com.sixb.note.dto.space.SpaceMdRequestDto;
import com.sixb.note.dto.space.SpaceMdResponseDto;
import com.sixb.note.dto.space.SpaceRequestDto;
import com.sixb.note.dto.space.SpaceResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Space;
import com.sixb.note.entity.User;
import com.sixb.note.exception.NotFoundException;
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

	public List<SpaceResponseDto> findAllSpaceDetails(long userId) {
		User users = userRepository.findUserById(userId);
		List<Space> spaces = spaceRepository.findSpaces(users.getUserId());

		return spaces.stream().map(space -> {
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
		}).collect(Collectors.toList());
	}

	public SpaceResponseDto findSpaceDetails(long userId, String spaceId) {
		User userInfo = userRepository.findUserById(userId);
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


	public SpaceResponseDto createSpace(SpaceRequestDto requestDto, long userId) {
		User user = userRepository.findUserById(userId);
		Space newSpace = new Space();
		newSpace.setTitle(requestDto.getTitle());
		newSpace.setSpaceId(IdCreator.create("s"));
		newSpace.setIsPublic(true);
		newSpace.setSpaceMd("SSAFY");
		LocalDateTime now = LocalDateTime.now();
		newSpace.setCreatedAt(now);
		newSpace.setUpdatedAt(now);

		Folder newFolder = new Folder();
		newFolder.setSpaceId(newSpace.getSpaceId());
		newFolder.setTitle("root");
		newFolder.setFolderId(IdCreator.create("f"));
		newFolder.setCreatedAt(now);
		newFolder.setUpdatedAt(now);

		newSpace.setFolders(Arrays.asList(newFolder));
		newSpace.setUsers(Arrays.asList(user));
		Space savedSpace = spaceRepository.save(newSpace);
//        userRepository.createJoinRelation(user.getId(), savedSpace.getId());
		return convertToSpaceResponseDto(savedSpace);
	}

	private SpaceResponseDto convertToSpaceResponseDto(Space space) {
		SpaceResponseDto responseDto = new SpaceResponseDto();
		responseDto.setSpaceId(space.getSpaceId());
		responseDto.setTitle(space.getTitle());
		responseDto.setIsPublic(true);
		responseDto.setSpaceMd("SSAFY");
		responseDto.setCreatedAt(LocalDateTime.now());
		responseDto.setUpdatedAt(LocalDateTime.now());
		responseDto.setUsers(new ArrayList<>());

		return responseDto;
	}

	//스페이스 이름 변경
	public void updateSpaceTitle(String spaceId, String newTitle) throws NotFoundException {
		Space space = spaceRepository.findSpaceById(spaceId);
		if (space == null) {
			throw new NotFoundException("스페이스 이름 수정 실패");
		}
		spaceRepository.updateSpaceTitle(spaceId, newTitle);
	}

	//스페이스 삭제
	public boolean deleteSpace(String spaceId) {
		if (spaceRepository.existsById(spaceId)) {
			spaceRepository.deleteById(spaceId);
			return true;
		}
		return false;
	}

	//스페이스 참가
	public void joinSpace(long userId, String spaceId) {
		User user = userRepository.findUserById(userId);
		Space space = spaceRepository.findSpaceById(spaceId);

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
}
