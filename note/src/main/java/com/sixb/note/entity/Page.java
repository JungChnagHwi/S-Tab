package com.sixb.note.entity;

import com.sixb.note.common.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Getter
@Setter
@Node("Page")
@Builder
public class Page extends BaseTimeEntity {

    @Id
    @Property("pageId")
    private String pageId;

    @Property("noteId")
    private String noteId;

    @Property("template")
    private String template;

    @Property("color")
    private String color;

    @Property("direction")
    private Integer direction;

    @Property("pdfUrl")
    private String pdfUrl;

    @Property("pdfPage")
    private Integer pdfPage;

    @Property("pageData")
    private String pageData;

    @Relationship(type = "NextPage")
    private Page NextPage;

}
