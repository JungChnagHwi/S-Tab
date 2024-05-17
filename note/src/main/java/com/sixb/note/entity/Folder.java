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

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Node("Folder")
public class Folder extends BaseTimeEntity {

	@Id
	@Property("folderId")
	private String folderId;

	@Property("spaceId")
	private String spaceId;

	@Property("title")
	private String title;

	@Relationship(type = "Hierarchy")
	private List<Folder> subFolders;

	@Relationship(type = "Hierarchy")
	private List<Note> notes;
}
