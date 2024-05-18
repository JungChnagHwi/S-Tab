package com.sixb.note.entity;

import com.sixb.note.entity.common.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@EqualsAndHashCode(callSuper = true)
@Data
@Node("Page")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
	private int direction;

	@Property("pdfUrl")
	private String pdfUrl;

	@Property("pdfPage")
	private int pdfPage;

	@Property("pageData")
	private String pageData;

	@Relationship(type = "NextPage")
	private Page NextPage;

}
