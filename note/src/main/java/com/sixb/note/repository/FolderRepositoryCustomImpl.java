package com.sixb.note.repository;

import com.sixb.note.dto.folder.FolderResponseDto;
import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static org.neo4j.cypherdsl.core.Cypher.*;

@Repository
@RequiredArgsConstructor
public class FolderRepositoryCustomImpl implements FolderRepositoryCustom {

	private final Driver driver;

	@Override
	public FolderResponseDto getFolderByName(long userId, String name, String spaceId) {
		Node user = node("User").named("u")
				.withProperties("userId", parameter("userId"));

		Node folder = node("Folder").named("f")
				.withProperties("spaceId", parameter("spaceId"));

		Node note = node("Note").named("n")
				.withProperties("spaceId", parameter("spaceId"));

		Relationship likeFolder = user.relationshipTo(folder, "Like");
		Relationship likeNote = user.relationshipTo(note, "Like");

		Statement folderStatement = match(folder)
				.where(folder.property("title").contains(parameter("name"))
						.and(folder.property("isDeleted").isFalse()))
				.optionalMatch(likeFolder)
				.returning(folder.property("folderId").as("folderId"),
						folder.property("title").as("title"),
						folder.property("createdAt").as("createdAt"),
						folder.property("updatedAt").as("updatedAt"),
						folder.property("isDeleted").as("isDeleted"),
						caseExpression()
								.when(exists(likeFolder)).then(literalTrue())
								.elseDefault(literalFalse()).as("isLiked"))
				.build();

		Statement noteStatement = match(note)
				.where(note.property("title").contains(parameter("name"))
						.and(note.property("isDeleted").isFalse()))
				.optionalMatch(likeNote)
				.returning(note.property("noteId").as("noteId"),
						note.property("title").as("title"),
						note.property("totalPageCnt").as("totalPageCnt"),
						note.property("createdAt").as("createdAt"),
						note.property("updatedAt").as("updatedAt"),
						note.property("isDeleted").as("isDeleted"),
						caseExpression()
								.when(exists(likeNote)).then(literalTrue())
								.elseDefault(literalFalse()).as("isLiked"))
				.build();

		try (Session session = driver.session()) {
			FolderResponseDto response = new FolderResponseDto();

			Result folderResult = session.run(folderStatement.getCypher(),
					Values.parameters("userId", userId, "spaceId", spaceId, "name", name));

			List<FolderResponseDto.FolderInfo> folders = new ArrayList<>();

			while (folderResult.hasNext()) {
				Record record = folderResult.next();

				FolderResponseDto.FolderInfo folderInfo = FolderResponseDto.FolderInfo.builder()
						.folderId(record.get("folderId").asString())
						.title(record.get("title").asString())
						.isLiked(record.get("isLiked").asBoolean())
						.createdAt(record.get("createdAt").asLocalDateTime())
						.updatedAt(record.get("updatedAt").asLocalDateTime())
						.isDeleted(record.get("isDeleted").asBoolean())
						.build();
				folders.add(folderInfo);
			}

			response.setFolders(folders);

			Result noteResult = session.run(noteStatement.getCypher(),
					Values.parameters("userId", userId, "spaceId", spaceId, "name", name));

			List<FolderResponseDto.NoteInfo> notes = new ArrayList<>();

			while (noteResult.hasNext()) {
				Record record = noteResult.next();

				FolderResponseDto.NoteInfo noteInfo = FolderResponseDto.NoteInfo.builder()
						.noteId(record.get("noteId").asString())
						.title(record.get("title").asString())
						.totalPageCnt(record.get("totalPageCnt").asInt())
						.createdAt(record.get("createdAt").asLocalDateTime())
						.updatedAt(record.get("updatedAt").asLocalDateTime())
						.isDeleted(record.get("isDeleted").asBoolean())
						.isLiked(record.get("isLiked").asBoolean())
						.build();
				notes.add(noteInfo);
			}

			response.setNotes(notes);

			return response;
		}
	}

}
