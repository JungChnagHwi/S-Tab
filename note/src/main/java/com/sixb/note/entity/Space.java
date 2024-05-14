package com.sixb.note.entity;

import com.sixb.note.common.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.*;

@Getter
@Setter
@Node("Space")
public class Space extends BaseTimeEntity {

    @Id
    @Property("spaceId")
    private String spaceId;

    @Property("rootFolderId")
    private String rootFolderId;

    @Property("title")
    private String title;

    @Property("isPublic")
    private Boolean isPublic;

    @Property("spaceMd")
    private String spaceMd;

    @Relationship(type = "Hierarchy")
    private List<Folder> folders;

    @Relationship(type = "Hierarchy")
    private List<Note> notes;

    @Relationship(type = "Join", direction = Relationship.Direction.INCOMING)
    private List<User> users;
}
