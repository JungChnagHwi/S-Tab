package com.sixb.note.entity;

import com.sixb.note.entity.common.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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

    @Relationship(type = "Join", direction = Relationship.Direction.INCOMING)
    private List<User> users;
}
