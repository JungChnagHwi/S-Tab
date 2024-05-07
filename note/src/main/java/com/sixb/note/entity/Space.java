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
@Node("Space")
public class Space extends BaseTimeEntity {

    @Id
    private String id;

    @Property("title")
    private String title;

    @Property("isPublic")
    private Boolean isPublic;

    @Relationship(type = "HAS_Hierarchy")
    private List<Folder> folders;

    @Relationship(type = "HAS_Hierarchy")
    private List<Note> notes;

    @Relationship(type = "HAS_SPACE", direction = Relationship.Direction.INCOMING)
    private List<User> users;
}
