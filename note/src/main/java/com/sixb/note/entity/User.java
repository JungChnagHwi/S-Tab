package com.sixb.note.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;

@Data
@Node("User")
public class User {

    @Id
    private long id;

    @Property("nickname")
    private String nickname;

    @Property("profileImg")
    private String profileImg;

    @Relationship(type = "HAS_SPACE")
    private List<Space> spaces;

    @Relationship(type = "HAS_Like")
    private List<Folder> folders;

    @Relationship(type = "HAS_Like")
    private List<Note> notes;
}