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
public interface NoteRepository extends Neo4jRepository<Note, UUID> {
    @Query("MATCH (f:Folder)-[:HAS_Hierarchy]->(n:Note) WHERE f.id = $folderId RETURN n")
    List<Note> findNotesByFolderId(@Param("folderId") UUID folderId);
}
