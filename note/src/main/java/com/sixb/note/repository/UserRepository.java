package com.sixb.note.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
//import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

import com.sixb.note.entity.User;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends Neo4jRepository<User, UUID> {
    List<User> findAll();

    @Query("MATCH (u:User)-[:HAS_SPACE]->(s:Space {id: $spaceId}) RETURN u")
    List<User> findUsersBySpaceId(UUID spaceId);

}
