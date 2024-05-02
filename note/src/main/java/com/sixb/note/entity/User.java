package com.sixb.note.entity;

import com.sixb.note.common.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Node("User")
public class User {

    @Id @GeneratedValue
    private UUID id;

    @Property("nickname")
    private String nickname;

    @Property("profileImg")
    private String profileImg;

    @Property("socialType")
    private String socialType;

    @Property("socialUserId")
    private String socialUserId;

    @Relationship(type = "HAS_SPACE")
    private List<Space> spaces;

    @Relationship(type = "HAS_Like")
    private List<Folder> folders;

    @Relationship(type = "HAS_Like")
    private List<Note> notes;
}