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

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        UserInfo userInfo = gitService.getUserInfo("userId");

        assertEquals(expected, userInfo);
    }

    @Test
    public void getUserRepo() throws JsonProcessingException {
        UserRepo repo = UserRepo.builder()
                .html_url("url")
                .name("name")
                .build();
        List<UserRepo> expected = List.of(repo);

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(new UserRepo[] {repo}))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        List<UserRepo> userRepos = gitService.getUserRepos("userId");

        assertEquals(expected, userRepos);
    }

    @Test
    public void getUserRepoReturnEmptyListOnNull() throws JsonProcessingException {

        mockWebServer.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(null))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        );

        List<UserRepo> userRepos = gitService.getUserRepos("userId");

        assertTrue(userRepos.isEmpty());
    }
}
