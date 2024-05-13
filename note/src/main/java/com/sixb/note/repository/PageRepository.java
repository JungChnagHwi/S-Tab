package com.sixb.note.repository;

import com.sixb.note.entity.Page;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends Neo4jRepository<Page, String> {

    @Query("MATCH (p:Page) WHERE p.isDeleted = true AND p.userId = $userId RETURN p")
    List<Page> findDeletedPages(@Param("userId") long userId);

    @Query("MATCH (p:Page) WHERE p.id = $pageId RETURN p")
    Page findPageById(@Param("pageId") String pageId);

    @Query("MATCH (u:User {id: $userId})-[:Like]->(p:Page) RETURN p")
    List<Page> findAllLikedPagesByUserId(@Param("userId") long userId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(p:Page {id: $itemId}) DELETE r")
    void deleteLikePage(@Param("userId") long userId, @Param("itemId") String itemId);

    @Query("MATCH (n: Note {id: $noteId})-[r:FirstPage]->(p: Page) RETURN p")
    Page findFirstPageByNoteId(@Param("noteId") String noteId);

    @Query("MATCH (p: Page {id: $pageId})-[r:NextPage]->(p1: Page) RETURN p1")
    Page getNextPageByPageId(@Param("pageId") String pageId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(p:Page {id: $pageId}) RETURN COUNT(*) > 0 AS liked")
    boolean isLikedByPageId(@Param("userId") long userId, @Param("pageId") String pageId);

    @Query("MATCH (p: Page {id: $pageId})-[r:NextPage]->(p1: Page) DELETE r")
    void deleteNextPageRelation(@Param("pageId") String pageId);

    @Query("MATCH (note:Note {id: $noteId})-[:FirstPage]->(firstPage:Page)\n" +
            "WITH note, firstPage\n" +
            "MATCH path=(firstPage)-[:NextPage*]->(page:Page)\n" +
            "WHERE NOT page.isDeleted\n" +
            "RETURN collect(firstPage) + collect(page) AS allPages")
    List<Page> findAllPagesByNoteId(@Param("noteId") String noteId);
}
