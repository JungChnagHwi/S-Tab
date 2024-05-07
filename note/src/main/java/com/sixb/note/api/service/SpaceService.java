package com.sixb.note.api.service;

import com.sixb.note.dto.space.SpaceRequestDto;
import com.sixb.note.dto.space.SpaceResponseDto;
import com.sixb.note.entity.Note;
import com.sixb.note.entity.Space;
import com.sixb.note.entity.User;
import com.sixb.note.repository.SpaceRepository;
import com.sixb.note.util.IdCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SpaceService {
    @Autowired
    private SpaceRepository spaceRepository;
    @Autowired
    private UserService userService;

    public List<SpaceResponseDto> findAllSpaceDetails() {
        return spaceRepository.findSpaces().stream().map(space -> {
            SpaceResponseDto dto = new SpaceResponseDto();
            dto.setSpaceId(space.getId());
            dto.setTitle(space.getTitle());
            dto.setIsPublic(space.getIsPublic());
            dto.setRootFolderId(null);
            dto.setCreateAt(space.getCreatedAt());
            dto.setUpdateAt(space.getModifiedAt());

            List<User> usersInSpace = userService.findUsersBySpaceId(space.getId());
            List<SpaceResponseDto.UserResponse> userResponses = usersInSpace.stream().map(user -> {
                SpaceResponseDto.UserResponse userResponse = new SpaceResponseDto.UserResponse();
                userResponse.setNickname(user.getNickname());
                userResponse.setProfileImg(user.getProfileImg());
                return userResponse;
            }).collect(Collectors.toList());

            dto.setUsers(userResponses);


            return dto;
        }).collect(Collectors.toList());
    }

    public SpaceResponseDto createSpace(SpaceRequestDto requestDto) {
        Space newSpace = new Space();
        newSpace.setTitle(requestDto.getTitle());

        String formattedId = IdCreator.create("s");
//        UUID formattedId = UUID.randomUUID();
        newSpace.setId(formattedId);

        newSpace.setIsPublic(true);
        LocalDateTime now = LocalDateTime.now();
        newSpace.setCreatedAt(now);
        newSpace.setModifiedAt(now);

        //보류 : 로그인 기능 구현 후 스페이스 생성 시 나 추가 해야 함

        Space savedSpace = spaceRepository.save(newSpace);

        return convertToSpaceResponseDto(savedSpace);
    }

    private SpaceResponseDto convertToSpaceResponseDto(Space space) {
        SpaceResponseDto responseDto = new SpaceResponseDto();
        responseDto.setSpaceId(space.getId());
        responseDto.setTitle(space.getTitle());
        responseDto.setIsPublic(true);
        responseDto.setRootFolderId(null);
        responseDto.setCreateAt(LocalDateTime.now());
        responseDto.setUpdateAt(LocalDateTime.now());
        responseDto.setUsers(new ArrayList<>());

        return responseDto;
    }

    //스페이스 이름 변경
    public boolean updateSpaceTitle(String spaceId, String newTitle) {
        Space space = spaceRepository.findSpaceById(spaceId);
        if (space != null) {
            space.setTitle(newTitle);
            spaceRepository.save(space);
            return true;
        }
        return false;
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
