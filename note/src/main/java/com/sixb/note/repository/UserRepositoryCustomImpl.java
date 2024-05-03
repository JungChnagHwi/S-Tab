package com.sixb.note.repository;

import com.sixb.note.dto.request.UserInfoRequestDto;
import com.sixb.note.dto.response.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Cypher;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

	private final Driver driver;

	@Override
	public Optional<UserInfoResponseDto> getUserInfo(long userId) {
		Node user = Cypher.node("User").named("u")
				.withProperties("id", Cypher.literalOf(userId));

		Node space = Cypher.node("Space").named("s")
				.withProperties("public", Cypher.literalOf(false));

		Node folder = Cypher.node("Folder").named("f");

		Statement statement = Cypher.match(user)
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
						.rootFolderId(record.get("id").asBoolean())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

	@Override
	public Optional<UserInfoResponseDto> updateUserInfo(long userId, UserInfoRequestDto request) {
		Node user = Cypher.node("User").named("u")
				.withProperties("id", Cypher.literalOf(userId));

		Node space = Cypher.node("Space").named("s")
				.withProperties("public", Cypher.literalOf(false));

		Node folder = Cypher.node("Folder").named("f");

		Statement statement = Cypher.match(user)
				.where(user.relationshipTo(space, "Join")
						.relationshipTo(folder, "Hierarchy"))
				.set(user.property("nickname").to(Cypher.literalOf(request.getNickname())))
				.set(user.property("profileImg").to(Cypher.literalOf(request.getProfileImg())))
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
						.rootFolderId(record.get("id").asBoolean())
						.build();
			}
		}

		return Optional.ofNullable(response);
	}

}
