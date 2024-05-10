package com.sixb.note.entity;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@Data
@Node("User")
public class User {

    @Id
    private long id;

    @Property("nickname")
    private String nickname;

    @Property("profileImg")
    private String profileImg;

    @Relationship(type = "Join")
    private List<Space> spaces;

    @Relationship(type = "Like")
    private List<Folder> folders;

    @Relationship(type = "Like")
    private List<Note> notes;

    @Relationship(type = "Like")
    private List<Page> pages;
}