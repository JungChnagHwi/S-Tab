package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import com.sixb.note.entity.Note;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends Neo4jRepository<Note, String> {
    @Query("MATCH (f:Folder)-[:HAS_Hierarchy]->(n:Note) WHERE f.id = $folderId RETURN n")
    List<Note> findNotesByFolderId(@Param("folderId") String folderId);
    @Query("MATCH (n:Note) WHERE n.id = $noteId RETURN n")
    Note findNoteById(@Param("noteId") String noteId);
    @Query("MATCH (n:Note) WHERE n.isDelete = 1 RETURN n")
    List<Note> findDeletedNotes();

    @Query("MATCH (u:User {id: $userId})-[:HAS_Like]->(n:Note) RETURN n")
    List<Note> findAllLikedNotesByUserId(@Param("userId") String userId);

    @Query("MATCH (u:User {id: $userId})-[r:HAS_Like]->(n:Note {id: $itemId}) DELETE r")
    void deleteLikeNote(@Param("userId") String userId, @Param("itemId") String itemId);
}
