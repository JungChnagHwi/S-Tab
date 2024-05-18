package com.sixb.note.entity;

import com.sixb.note.entity.common.BaseTimeEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

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
	@Builder.Default
	private Boolean isPublic = false;

	@Property("spaceMd")
	private String spaceMd;

	@Relationship(type = "Hierarchy")
	private Folder folder;

	@Relationship(type = "Join", direction = Relationship.Direction.INCOMING)
	private List<User> users;
}
