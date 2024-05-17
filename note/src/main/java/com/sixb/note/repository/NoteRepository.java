package com.sixb.note.repository;

import com.sixb.note.entity.Note;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public interface NoteRepository extends Neo4jRepository<Note, String>, NoteRepositoryCustom {

	@Query("MATCH (f:Folder)-[:Hierarchy]->(n:Note) WHERE f.folderId = $folderId AND n.isDeleted = false RETURN n")
	List<Note> findNotesByFolderId(@Param("folderId") String folderId);

	@Query("MATCH (s:Space {spaceId: $spaceId})-[:Hierarchy]->(n:Note) WHERE n.isDeleted = false RETURN n")
	List<Note> findNotesBySpaceId(@Param("spaceId") String spaceId);

	@Query("MATCH (n:Note) WHERE n.noteId = $noteId RETURN n")
	Optional<Note> findNoteById(@Param("noteId") String noteId);

	@Query("MATCH (u:User {userId: $userId})-[:Join]->(s:Space)-[:Hierarchy*]->(f:Folder)-[:Hierarchy]->(n:Note) " +
			"WHERE n.isDeleted = true " +
			"MATCH (f)-[:Hierarchy]->(n) " +
			"WHERE f.isDeleted = false " +
			"RETURN n")
	List<Note> findDeletedNotes(@Param("userId") long userId);

	@Query("MATCH (u:User {userId: $userId})-[:Like]->(n:Note) WHERE n.isDeleted = false RETURN n")
	List<Note> findAllLikedNotesByUserId(@Param("userId") long userId);

	@Query("MATCH (u:User {userId: $userId})-[:Like]->(n:Note) WHERE n.noteId IN $noteIds RETURN n.noteId")
	List<String> findLikedNoteIdsByUserId(@Param("userId") long userId, @Param("noteIds") List<String> noteIds);

	@Query("MATCH (u:User {userId: $userId})-[r:Like]->(n:Note {noteId: $itemId}) DELETE r")
	void deleteLikeNote(@Param("userId") long userId, @Param("itemId") String itemId);

	@Query("MATCH (n:Note {noteId: $noteId}) SET n.title = $newTitle RETURN n")
	void updateNoteTitle(String noteId, String newTitle);

	@Query("MATCH (n:Note {noteId: $noteId})<-[or:Hierarchy]-(of:Folder) " +
			"MATCH (nf:Folder {folderId: $parentFolderId}) " +
			"CREATE (n)<-[nr:Hierarchy]-(nf) " +
			"DELETE or")
	void relocateNote(String noteId, String parentFolderId);

	@Query("MATCH (n:Note {noteId: $noteId}) RETURN n.spaceId")
	String findSpaceIdByNoteId(String noteId);

	@Query("MATCH (n:Note {noteId: $noteId})-[:NextPage*]->(p:Page) " +
			"WHERE p.updatedAt = n.updatedAt " +
			"SET n.isDeleted = false, " +
			"    n.updatedAt = $now, " +
			"    p.isDeleted = false, " +
			"    p.updatedAt = $now")
	void recover(String noteId, LocalDateTime now);

}
