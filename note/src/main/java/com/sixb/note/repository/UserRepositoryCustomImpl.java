package com.sixb.note.repository;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.UserInfoResponseDto;
import com.sixb.note.util.IdCreator;
import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.neo4j.cypherdsl.core.Cypher.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final Driver driver;

	@Override
	public Optional<UserInfoResponseDto> getUserInfo(long userId) {
		Node user = node("User").named("u")
				.withProperties("id", literalOf(userId));

		Node space = node("Space").named("s")
				.withProperties("public", literalOf(false));

		Node folder = node("Folder").named("f");

		Statement statement = match(user)
				.where(user.relationshipTo(space, "Join").relationshipTo(folder, "Hierarchy"))
				.returning(
						user.property("nickname"),
						user.property("profileImg"),
						folder.property("id"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("id").asString())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

	@Override
	@Transactional
	public UserInfoResponseDto signup(long userId, UserInfoRequestDto request) {
		LocalDateTime now = LocalDateTime.now();

		String spaceId = IdCreator.create("s");
		String folderId = IdCreator.create("f");

		Node user = node("User").named("u")
				.withProperties(
						"id", literalOf(userId),
						"nickname", literalOf(request.getNickname()),
						"profileImg", literalOf(request.getProfileImg()),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalOf(false));

		Node space = node("Space").named("s")
				.withProperties(
						"id", literalOf(spaceId),
						"title", literalOf(request.getNickname() + "의 스페이스"),
						"public", literalOf(false),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalOf(false));

		Node folder = node("Folder").named("f")
				.withProperties(
						"id", literalOf(folderId),
						"spaceId", literalOf(spaceId),
						"title", literalOf("root"),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalOf(false));

		Relationship join = user.relationshipTo(space, "Join");
		Relationship hierarchy = space.relationshipTo(folder, "Hierarchy");

		Statement statement = create(user)
				.create(space)
				.create(folder)
				.create(join)
				.create(hierarchy)
				.returning(
						user.property("nickname"),
						user.property("profileImg"),
						folder.property("id"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("id").asString())
						.build();
			}
		}

		return response;
	}

	@Override
	public Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request) {
		Node user = node("User").named("u")
				.withProperties("id", literalOf(userId));

		Node space = node("Space").named("s")
				.withProperties("public", literalOf(false));

		Node folder = node("Folder").named("f");

		Statement statement = match(user)
				.where(user.relationshipTo(space, "Join")
						.relationshipTo(folder, "Hierarchy"))
				.set(user.property("nickname").to(literalOf(request.getNickname())))
				.set(user.property("profileImg").to(literalOf(request.getProfileImg())))
				.returning(
						user.property("nickname"),
						user.property("profileImg"),
						folder.property("id"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("id").asString())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

}
