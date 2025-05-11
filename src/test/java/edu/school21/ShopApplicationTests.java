package edu.school21;

import edu.school21.utils.RandomModels;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@Import(RandomModels.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShopApplicationTests {
    @LocalServerPort
    protected int port;
    @Autowired
    protected RandomModels randomModels;
    protected static PostgreSQLContainer postgres;

    @BeforeAll
    public static void init() {
        postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.1-alpine"))
                .withDatabaseName("spring.datasource.name")
                .withUsername("spring.datasource.username")
                .withPassword("spring.datasource.password")
                .withInitScript("init.sql");
        postgres.start();
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri("http://localhost")
                .build();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
}
