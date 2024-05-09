package com.sixb.note.repository;

import lombok.RequiredArgsConstructor;
import org.neo4j.cypherdsl.core.Node;
import org.neo4j.cypherdsl.core.Relationship;
import org.neo4j.cypherdsl.core.Statement;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import static org.neo4j.cypherdsl.core.Cypher.*;

// 나중을 위해 일단 남겨둘게요! 읽지 않아도 됨

@Repository
@RequiredArgsConstructor
public class PageRepositoryCustomImpl implements PageRepositoryCustom {

    private final Driver driver;

//    @Override
//    public boolean isLastPage(String pageId) {
//        Node page = node("Page").named("p");
//        Node nextPage = node("Page").named("n");
//
//        Statement statement = match(page)
//                .where(page.property("id").isEqualTo(literalOf(pageId)))
//                .optionalMatch(page.relationshipTo(nextPage, "NextPage"))
//                .returning(nextPage.property("id").isNull().as("isLastPage"))
//                .build();
//
//        boolean isLastPage;
//
//        try (Session session = driver.session()){
//            Record record = session.run(statement.getCypher()).single();
//
//            isLastPage = record.get("isLastPage").asBoolean();
//        } catch (NoSuchElementException e) {
//            return false;
//        }
//        return isLastPage;
//    }


    // 현재 새로운 페이지 생성과 나눠져 있는데, 나중에 합칠 예정
    @Override
    public void connectNextPage(String pageId, String nextPageId) {

        // page 찾기
        Node page = node("Page").named("p").withProperties("id", literalOf(pageId));
        Node nextPage = node("Page").named("n").withProperties("id", literalOf(nextPageId));
        Node relPage = anyNode("Page");

        // 이어진페이지 찾기
        Statement findRelPage = optionalMatch(page.relationshipTo(relPage, "NextPage"))
                .returning(relPage)
                .build();

        // 관계 만들기1(page-nextPage)
        Relationship c1 = page.relationshipTo(nextPage, "NextPage");
        Statement connectStatement1 = create(c1).build();
        // 관계 만들기2 (nextPage-relPage)
        Relationship c2 = nextPage.relationshipTo(relPage, "NextPage");
        Statement connectStatement2 = create(c2).build();

        try (Session session = driver.session()){
            Result relPageResult = session.run(findRelPage.getCypher());
            if (relPageResult.hasNext()){ // 마지막 페이지가 아닌경우
                // 관계 삭제
                Statement deleteStatement = match(page.relationshipTo(relPage, "NextPage").named("rel"))
                        .delete("rel")
                        .build();
                session.run(deleteStatement.getCypher());
                session.run(connectStatement2.getCypher());
            }
            session.run(connectStatement1.getCypher());
        }

    }

//    @Override
//    public Page getNextPageId(String pageId) {
//        Node page = node("Page").named("p").withProperties("id", literalOf(pageId));
//        Node nextPage = node("Page").named("n");
//        Statement statement = match(page.relationshipTo(nextPage, "NextPage"))
//                .returning(nextPage)
//                .build();
//
//        try (Session session = driver.session()) {
//            Result result = session.run(statement.getCypher());
//            return ?? // 어떻게 해야하지?
//        }
//
//    }

//    public Page findFirstPageByNoteId(String noteId) {
//        Node note = node("Page").named("p").withProperties("id", literalOf(noteId));
//    }
//    @Override
}
