package com.sixb.note.repository;

import com.sixb.note.dto.user.UserInfoRequestDto;
import com.sixb.note.dto.user.NicknameResponseDto;
import com.sixb.note.dto.user.UserInfoResponseDto;
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
				.withProperties("userId", literalOf(userId));

		Node space = node("Space").named("s")
				.withProperties("isPublic", literalFalse());

		Node folder = node("Folder").named("f");

		Statement statement = match(user, space, folder)
				.where(user.relationshipTo(space, "Join")
						.relationshipTo(folder, "Hierarchy"))
				.returning(
						user.property("nickname").as("nickname"),
						user.property("profileImg").as("profileImg"),
						folder.property("folderId").as("rootFolderId"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("rootFolderId").asString())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

	@Override
	public boolean isSignedUpUser(long userId) {
		Node user = node("User").named("u");

		Statement statement = match(user)
				.where(user.property("userId").isEqualTo(literalOf(userId)))
				.returning(count(user).gt(literalOf(0)).as("result"))
				.build();

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			Record record = result.next();
			return record.get("result").asBoolean();
		}
	}

	@Override
	public UserInfoResponseDto signup(long userId, UserInfoRequestDto request) {
		LocalDateTime now = LocalDateTime.now();

		String spaceId = IdCreator.create("s");
		String folderId = IdCreator.create("f");

		Node user = node("User").named("u")
				.withProperties(
						"userId", literalOf(userId),
						"nickname", literalOf(request.getNickname()),
						"profileImg", literalOf(request.getProfileImg()),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalFalse());

		Node space = node("Space").named("s")
				.withProperties(
						"spaceId", literalOf(spaceId),
						"title", literalOf("나의 스페이스"),
						"isPublic", literalFalse(),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalFalse());

		Node folder = node("Folder").named("f")
				.withProperties(
						"folderId", literalOf(folderId),
						"spaceId", literalOf(spaceId),
						"title", literalOf("root"),
						"createdAt", literalOf(now),
						"updatedAt", literalOf(now),
						"isDeleted", literalFalse());

		Relationship join = user.relationshipTo(space, "Join");
		Relationship hierarchy = space.relationshipTo(folder, "Hierarchy");

		Statement statement = create(user)
				.create(space)
				.create(folder)
				.create(join)
				.create(hierarchy)
				.returning(
						user.property("nickname").as("nickname"),
						user.property("profileImg").as("profileImg"),
						folder.property("folderId").as("rootFolderId"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("rootFolderId").asString())
						.build();
			}
		}

		return response;
	}

	@Override
	public Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request) {
		Node user = node("User").named("u")
				.withProperties("userId", literalOf(userId));

		Node space = node("Space").named("s")
				.withProperties("isPublic", literalFalse());

		Node folder = node("Folder").named("f");

		Statement statement = match(user, space, folder)
				.where(user.relationshipTo(space, "Join")
						.relationshipTo(folder, "Hierarchy"))
				.set(
						user.property("nickname").to(literalOf(request.getNickname())),
						user.property("profileImg").to(literalOf(request.getProfileImg())),
						user.property("updatedAt").to(literalOf(LocalDateTime.now())))
				.returning(
						user.property("nickname").as("nickname"),
						user.property("profileImg").as("profileImg"),
						folder.property("folderId").as("rootFolderId"))
				.build();

		UserInfoResponseDto response = null;

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			if (result.hasNext()) {
				Record record = result.next();
				response = UserInfoResponseDto.builder()
						.nickname(record.get("nickname").asString())
						.profileImg(record.get("profileImg").asString())
						.rootFolderId(record.get("rootFolderId").asString())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

	@Override
	public NicknameResponseDto findNicknameCount(String nickname) {
		Node user = node("User").named("u");

		Statement statement = match(user)
				.where(user.property("nickname").isEqualTo(literalOf(nickname)))
				.returning(count(user).as("result"))
				.build();

		try (Session session = driver.session()) {
			Result result = session.run(statement.getCypher());
			Record record = result.next();
			return NicknameResponseDto.builder()
					.result(record.get("result").asInt())
					.build();
		}
	}

}
