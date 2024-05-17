package com.sixb.note.repository;

import com.sixb.note.entity.Space;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SpaceRepository extends Neo4jRepository<Space, String>, SpaceRepositoryCustom {

	@Query("MATCH (s:Space) WHERE s.spaceId = $spaceId RETURN s")
	Space findSpaceById(@Param("spaceId") String spaceId);

	@Query("MATCH (u:User {userId: $userId})-[:Join]->(s:Space) WHERE s.isPublic = true RETURN s")
	List<Space> findSpaces(@Param("userId") long userId);

	@Query("MATCH (u:User {userId: $userId})-[:Join]->(s:Space) WHERE s.spaceId = $spaceId RETURN s")
	Space findSpaceByIdAndUserId(@Param("spaceId") String spaceId, @Param("userId") long userId);

	@Query("MATCH (s:Space {spaceId: $spaceId}) SET s.title = $newTitle RETURN s")
	void updateSpaceTitle(String spaceId, String newTitle);

	@Query("MATCH (u:User {userId: $userId})-[r:Join]->(s:Space {spaceId: $spaceId}) DELETE r")
	void removeUserFromSpace(@Param("userId") long userId, @Param("spaceId") String spaceId);

	@Query("MATCH (u:User {userId: $userId}), (s:Space {spaceId: $spaceId}) " +
			"OPTIONAL MATCH (u)-[j:Join]->(s) " +
			"RETURN count(j) > 0 AS joined")
	boolean isJoinedUser(long userId, String spaceId);

	@Query("MATCH (s:Space {spaceId: $spaceId}) RETURN s.isPublic")
	boolean isPublicSpace(String spaceId);

}
