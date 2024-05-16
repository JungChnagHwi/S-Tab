package com.sixb.note.repository;

import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.cypherdsl.core.internal.RelationshipTypes;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static org.neo4j.cypherdsl.core.Cypher.*;

@Repository
@RequiredArgsConstructor
public class SpaceRepositoryCustomImpl implements SpaceRepositoryCustom {

	private final Driver driver;

	@Override
	public void deleteSpace(String spaceId) {
		LocalDateTime now = LocalDateTime.now();

		Node space = node("Space").named("s")
				.withProperties("spaceId", parameter("spaceId"));
		Node f = anyNode("f");
		Node n = anyNode("n");
		Node p = anyNode("p");
		Relationship r1 = space.relationshipBetween(f, "Hierarchy").unbounded();
		Relationship r2 = f.relationshipTo(n, "Hierarchy");
		Relationship r3 = n.relationshipBetween(p, "NextPage").unbounded();

		Statement statement = match(space, f, n, p)
				.match(r1)
				.match(r2)
				.match(r3)
				.set(space.property("isDeleted"), literalTrue(),
						space.property("updatedAt"), literalOf(now),
						f.property("isDeleted"), literalTrue(),
						f.property("updatedAt"), literalOf(now),
						n.property("isDeleted"), literalTrue(),
						n.property("updatedAt"), literalOf(now),
						p.property("isDeleted"), literalTrue(),
						p.property("updatedAt"), literalOf(now))
                .build();

		try (Session session = driver.session()) {
			session.run(statement.getCypher(),
					Values.parameters("spaceId", spaceId));
		}
	}

}
