package com.sixb.note.entity;

import com.sixb.note.entity.common.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.*;

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
