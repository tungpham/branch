package com.example.demo.sao;

import com.example.demo.entity.UserInfoEntity;
import com.example.demo.entity.UserRepoEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitServiceTest {

    static GitService gitService;
    static ObjectMapper objectMapper = new ObjectMapper();

    static MockWebServer mockWebServer;

    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/");

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        gitService = new GitService(WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper)))
                .build(), url.url().toString());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getUserInfo() throws JsonProcessingException {
        UserInfoEntity expected = UserInfoEntity.builder()
                .url("url")
                .name("name")
                .login("userId")
                .email("email")
                .avatar_url("avatar")
                .location("location")
                .created_at(Instant.now())
                .build();
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expected))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        Mono<UserInfoEntity> userInfo = gitService.getUserInfoAsync("userId");

        assertEquals(expected, userInfo.block());
    }

    @Test
    public void getUserRepo() throws JsonProcessingException {
        UserRepoEntity repo = UserRepoEntity.builder()
                .html_url("url")
                .name("name")
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new UserRepoEntity[] {repo}))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        Mono<UserRepoEntity[]> userRepos = gitService.getUserReposAsync("userId");

        assertEquals(repo, userRepos.block()[0]);
    }
}
