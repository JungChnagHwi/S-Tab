package com.sixb.note.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.*;
import com.sixb.note.common.BaseTimeEntity;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Node("Note")
public class Note extends BaseTimeEntity {

    @Id @GeneratedValue
    private UUID id;

    @Property("spaceId")
    private UUID spaceId;

    @Property("title")
    private String title;

    @Property("totalPageCnt")
    private int totalPageCnt;

    @Relationship(type = "HAS_Page", direction = Relationship.Direction.OUTGOING)
    private List<Page> pages;
}
