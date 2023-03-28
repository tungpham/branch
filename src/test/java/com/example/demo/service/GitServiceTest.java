package com.example.demo.service;

import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GitServiceTest {

    static GitService gitService;
    ObjectMapper objectMapper = new ObjectMapper();

    static MockWebServer mockWebServer;

    @BeforeAll
    public static void setup() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        HttpUrl url = mockWebServer.url("/");
        gitService = new GitService(WebClient.create(), url.url().toString());
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getUserInfo() throws JsonProcessingException {
        UserInfo expected = UserInfo.builder()
                .url("url")
                .name("name")
                .login("userId")
                .email("email")
                .avatar_url("avatar")
                .location("location")
                .created_at("created_at")
                .build();
        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expected))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        Mono<UserInfo> userInfo = gitService.getUserInfoAsync("userId");

        assertEquals(expected, userInfo.block());
    }

    @Test
    public void getUserRepo() throws JsonProcessingException {
        UserRepo repo = UserRepo.builder()
                .html_url("url")
                .name("name")
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new UserRepo[] {repo}))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        Mono<UserRepo[]> userRepos = gitService.getUserReposAsync("userId");

        assertEquals(repo, userRepos.block()[0]);
    }
}
