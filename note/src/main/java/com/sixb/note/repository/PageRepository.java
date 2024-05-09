package com.sixb.note.repository;

import com.sixb.note.entity.Page;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends Neo4jRepository<Page, String> {

    @Query("MATCH (p) WHERE p.isDelete = true RETURN p")
    List<Page> findDeletedPages();

    @Query("MATCH (p:Page) WHERE p.id = $pageId RETURN p")
    Page findPageById(@Param("pageId") String pageId);

    @Query("MATCH (u:User {id: $userId})-[:Like]->(p:Page) RETURN p")
    List<Page> findAllLikedPagesByUserId(@Param("userId") String userId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(p:Page {id: $itemId}) DELETE r")
    void deleteLikePage(@Param("userId") String userId, @Param("itemId") String itemId);

    @Query("MATCH (n: Note {id: $noteId})-[r:FirstPage]->(p: Page) RETURN p")
    Page findFirstPageByNoteId(@Param("noteId") String noteId);

    // pageId로 nextPage 찾는건데, 기본 제공 함수가 있어서 일단 그걸 사용해볼 예정!
//    @Query("MATCH (p: Page {id: $pageId})-[r:NextPage]->(n: Page) RETURN n")
//    Page findNextPageByPageId(@Param("pageId") String pageId);

    @Query("MATCH (u:User {id: $userId})-[r:Like]->(p:Page {id: $pageId}) RETURN COUNT(*) > 0 AS liked")
    boolean isLikedByPageId(@Param("userId") String userId, @Param("pageId") String pageId);
}
