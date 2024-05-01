package com.sixb.note.entity;

import com.sixb.note.common.BaseTimeEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Node("Folder")
public class Folder extends BaseTimeEntity {

    @Id @GeneratedValue
    private UUID id;

    @Property("spaceId")
    private String spaceId;

    @Property("title")
    private String title;

    @Relationship(type = "HAS_Hierarchy")
    private List<Folder> subFolders;

    @Relationship(type = "HAS_Hierarchy")
    private List<Note> notes;
}
