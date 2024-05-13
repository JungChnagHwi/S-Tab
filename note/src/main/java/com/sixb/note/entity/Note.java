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

    @Id
    private String id;

    @Property("spaceId")
    private String spaceId;

    @Property("title")
    private String title;

    @Property("totalPageCnt")
    private int totalPageCnt;

    @Relationship(type = "NextPage")
    private Page page;
}
