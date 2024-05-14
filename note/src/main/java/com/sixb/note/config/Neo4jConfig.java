package com.sixb.note.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class Neo4jConfig {

	@Value("${spring.data.neo4j.uri}")
	private String uri;

	@Value("${spring.data.neo4j.username}")
	private String username;

	@Value("${spring.data.neo4j.password}")
	private String password;

	@Bean
	public Driver neo4jDriver() {
		return GraphDatabase.driver(uri, AuthTokens.basic(username, password));
	}

	@Bean
	@Primary
	public Neo4jTransactionManager transactionManager(Driver driver) {
		return new Neo4jTransactionManager(driver);
	}

}
