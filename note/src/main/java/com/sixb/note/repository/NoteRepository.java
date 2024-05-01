package com.sixb.note.repository;

import com.sixb.note.entity.Note;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NoteRepository extends Neo4jRepository<Note, UUID> {
}
