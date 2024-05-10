package com.sixb.note.api.service;

import com.sixb.note.dto.space.SpaceRequestDto;
import com.sixb.note.dto.space.SpaceResponseDto;
import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Space;
import com.sixb.note.entity.User;
import com.sixb.note.exception.NotFoundException;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.repository.UserRepository;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public List<SpaceResponseDto> findAllSpaceDetails() {

        List<Space> spaces = spaceRepository.findSpaces();

        return spaces.stream().map(space -> {
            SpaceResponseDto dto = new SpaceResponseDto();
            dto.setSpaceId(space.getId());
            dto.setTitle(space.getTitle());
            dto.setIsPublic(space.getIsPublic());
            dto.setCreatedAt(space.getCreatedAt());
            dto.setUpdatedAt(space.getUpdatedAt());

            List<SpaceResponseDto.UserResponse> userResponses = space.getUsers().stream().map(user -> {
                SpaceResponseDto.UserResponse userResponse = new SpaceResponseDto.UserResponse();
                userResponse.setNickname(user.getNickname());
                userResponse.setProfileImg(user.getProfileImg());
                return userResponse;
            }).collect(Collectors.toList());

            dto.setUsers(userResponses);

            return dto;
        }).collect(Collectors.toList());
    }

    public SpaceResponseDto createSpace(SpaceRequestDto requestDto, long userId) {
        User user = userRepository.findUserById(userId);
        Space newSpace = new Space();
        newSpace.setTitle(requestDto.getTitle());
        newSpace.setId(IdCreator.create("s"));
        newSpace.setIsPublic(true);
        LocalDateTime now = LocalDateTime.now();
        newSpace.setCreatedAt(now);
        newSpace.setUpdatedAt(now);

        //보류 : 로그인 기능 구현 후 스페이스 생성 시 나 추가 해야 함
        newSpace.setUsers(Arrays.asList(user));
        Space savedSpace = spaceRepository.save(newSpace);
        return convertToSpaceResponseDto(savedSpace);
    }

    private SpaceResponseDto convertToSpaceResponseDto(Space space) {
        SpaceResponseDto responseDto = new SpaceResponseDto();
        responseDto.setSpaceId(space.getId());
        responseDto.setTitle(space.getTitle());
        responseDto.setIsPublic(true);
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
}
