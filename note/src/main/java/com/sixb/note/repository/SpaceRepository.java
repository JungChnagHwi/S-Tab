package com.sixb.note.repository;

import com.sixb.note.entity.Space;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpaceRepository extends Neo4jRepository<Space, UUID> {
    Space findSpaceById(UUID spaceId);
}
