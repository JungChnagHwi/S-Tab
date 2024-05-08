package com.sixb.note.repository;

import com.sixb.note.entity.Space;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

import com.sixb.note.entity.User;

import java.util.*;

@Repository
public interface UserRepository extends Neo4jRepository<User, String>, UserRepositoryCustom {

    @Query("MATCH (u:User)-[:Join]->(s:Space {id: $spaceId}) RETURN u")
    List<User> findUsersBySpaceId(String spaceId);

    @Query("MATCH (u:User) WHERE u.id = $userId RETURN u")
    User findUserById(@Param("userId") String userId);

}
