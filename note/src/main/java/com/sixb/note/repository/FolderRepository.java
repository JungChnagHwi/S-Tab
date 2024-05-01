package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, UUID> {
}
