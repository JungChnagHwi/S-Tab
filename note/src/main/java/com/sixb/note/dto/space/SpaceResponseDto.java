package com.sixb.note.dto.space;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SpaceResponseDto {
    private UUID spaceId;
    private String title;
    private Boolean isPublic;
    private String rootFolderId;
    private List<UserResponse> users;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    @Getter
    @Setter
    public static class UserResponse {
        private String nickname;
        private String profileImg;
    }
}
