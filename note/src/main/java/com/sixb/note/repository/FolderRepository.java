package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, UUID> {
    @Query("MATCH (p:Folder)-[:HAS_Hierarchy]->(c:Folder) WHERE p.id = $folderId RETURN c")
    List<Folder> findSubFoldersByFolderId(UUID folderId);
}

