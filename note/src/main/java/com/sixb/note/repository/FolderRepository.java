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
    @Query("MATCH (p:Folder)-[:Hierarchy]->(c:Folder) WHERE p.id = $folderId AND c.isDeleted = false RETURN c")
    List<Folder> findSubFoldersByFolderId(String folderId);
    @Query("MATCH (s:Space {id: $spaceId})-[:Hierarchy]->(f:Folder) WHERE f.isDeleted = false RETURN f")
    List<Folder> findFoldersBySpaceId(@Param("spaceId") String spaceId);

    @Query("MATCH (p:Folder)-[:Hierarchy]->(c:Folder) WHERE c.id = $folderId RETURN p")
    Folder findParentFolderByFolderId(@Param("folderId") String folderId);

    @Query("MATCH (f:Folder) WHERE f.isDeleted = true RETURN f")
    List<Folder> findDeletedFolders(@Param("userId") long userId);

    @Query("MATCH (f:Folder) WHERE f.id = $folderId RETURN f")
    Folder findFolderById(@Param("folderId") String folderId);

    @Query("MATCH (u:User {id: $userId})-[:Like]->(f:Folder) RETURN f")
    List<Folder> findAllLikedFoldersByUserId(@Param("userId") long userId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(f:Folder {id: $itemId}) DELETE r")
    void deleteLikeFolder(@Param("userId") long userId, @Param("itemId") String itemId);

    @Query("MATCH (f:Folder {id: $folderId}) SET f.title = $newTitle RETURN f")
    void updateFolderTitle(String folderId, String newTitle);
}

