package com.sixb.note.repository;

import com.sixb.note.entity.Folder;
import org.springframework.data.repository.query.Param;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, String> {
    @Query("MATCH (p:Folder)-[:HAS_Hierarchy]->(c:Folder) WHERE p.id = $folderId RETURN c")
    List<Folder> findSubFoldersByFolderId(String folderId);

    @Query("MATCH (p:Folder)-[:HAS_Hierarchy]->(c:Folder) WHERE c.id = $folderId RETURN p")
    Folder findParentFolderByFolderId(@Param("folderId") String folderId);

    @Query("MATCH (f:Folder) WHERE f.isDelete = 1 RETURN f")
    List<Folder> findDeletedFolders();

    @Query("MATCH (f:Folder) WHERE f.id = $folderId RETURN f")
    Folder findFolderById(@Param("folderId") String folderId);
}

