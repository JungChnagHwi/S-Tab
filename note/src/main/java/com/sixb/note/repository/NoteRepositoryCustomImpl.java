package com.sixb.note.repository;

import com.sixb.note.dto.note.CreateNoteRequestDto;
import com.sixb.note.dto.note.CreateNoteResponseDto;
import com.sixb.note.exception.FolderNotFoundException;
import com.sixb.note.util.Const;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.exceptions.NoSuchRecordException;
import org.neo4j.driver.types.MapAccessor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static org.neo4j.cypherdsl.core.Cypher.*;

@Repository
@RequiredArgsConstructor
public class NoteRepositoryCustomImpl implements NoteRepositoryCustom {

	private final Driver driver;

	@Override
	public Optional<CreateNoteResponseDto> createNote(CreateNoteRequestDto request) throws FolderNotFoundException {
		LocalDateTime now = LocalDateTime.now();

		String spaceId = getSpaceId(request.getParentFolderId());
		String noteId = IdCreator.create("n");
		String pageId = IdCreator.create("p");

		Node folder = node("Folder").named("f")
				.withProperties("folderId", literalOf(request.getParentFolderId()));

		Node note = node("Note").named("n")
				.withProperties(
						"noteId", literalOf(noteId),
						"spaceId", literalOf(spaceId),
						"title", literalOf(request.getTitle()),
						"totalPageCnt", literalOf(1),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalFalse());

		Node page = node("Page").named("p")
				.withProperties(
						"pageId", literalOf(pageId),
						"noteId", literalOf(noteId),
						"template", literalOf(request.getTemplate()),
						"color", literalOf(request.getColor()),
						"direction", literalOf(request.getDirection()),
						"pdfUrl", literalOf(null),
						"pdfPage", literalOf(0),
						"pageData", literalOf(Const.INIT_PAGE_DATA),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalFalse());

		Relationship hierarchy = folder.relationshipTo(note, "Hierarchy");
		Relationship nextPage = note.relationshipTo(page, "NextPage");

		Statement statement = match(folder)
				.create(note, page, hierarchy, nextPage)
				.returning(note, page)
				.build();

		CreateNoteResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();

				MapAccessor noteNode = record.get("n").asNode();
				MapAccessor pageNode = record.get("p").asNode();

				CreateNoteResponseDto.PageDto pageDto = CreateNoteResponseDto.PageDto.builder()
						.pageId(pageNode.get("pageId").asString())
						.color(pageNode.get("color").asString())
						.template(pageNode.get("template").asString())
						.direction(pageNode.get("direction").asInt())
						.isBookmarked(false)
						.createdAt(pageNode.get("createdAt").asLocalDateTime())
						.updatedAt(pageNode.get("updatedAt").asLocalDateTime())
						.isDeleted(pageNode.get("isDeleted").asBoolean())
						.build();

				response = CreateNoteResponseDto.builder()
						.noteId(noteNode.get("noteId").asString())
						.title(noteNode.get("title").asString())
						.totalPageCnt(noteNode.get("totalPageCnt").asInt())
						.isLiked(false)
						.page(pageDto)
						.createdAt(noteNode.get("createdAt").asLocalDateTime())
						.updatedAt(noteNode.get("updatedAt").asLocalDateTime())
						.isDeleted(noteNode.get("isDeleted").asBoolean())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

	private String getSpaceId(String folderId) throws FolderNotFoundException {
		Node folder = node("Folder").named("f")
				.withProperties("folderId", literalOf(folderId));

		Statement statement = match(folder)
				.returning(folder.property("spaceId").as("spaceId"))
				.build();

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			return result.single().get("spaceId").asString();
		} catch (NoSuchRecordException e) {
			throw new FolderNotFoundException("존재하지 않는 폴더입니다.");
		}
	}

}
