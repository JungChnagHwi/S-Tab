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
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Node("Note")
public class Note extends BaseTimeEntity {

	@Id
	@Property("noteId")
	private String noteId;

	@Property("spaceId")
	private String spaceId;

	@Property("title")
	private String title;

	@Property("totalPageCnt")
	private int totalPageCnt;

	@Relationship(type = "NextPage")
	private Page page;

}
