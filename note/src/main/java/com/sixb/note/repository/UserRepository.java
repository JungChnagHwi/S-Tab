package com.sixb.note.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.neo4j.repository.query.Query;

import com.sixb.note.entity.User;

import java.util.*;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long>, UserRepositoryCustom {

    @Query("MATCH (u:User)-[:HAS_SPACE]->(s:Space {id: $spaceId}) RETURN u")
    List<User> findUsersBySpaceId(String spaceId);

}
