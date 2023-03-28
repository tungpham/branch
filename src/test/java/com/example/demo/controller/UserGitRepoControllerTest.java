package com.example.demo.controller;

import com.example.demo.dto.RepoDto;
import com.example.demo.dto.UserGitRepoResponse;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserRepo;
import com.example.demo.service.GitService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(UserGitController.class)
public class UserGitRepoControllerTest {

    @MockBean
    private GitService gitService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getUserRepo() {
        Instant createdAt = Instant.now();
        UserInfo userInfoEntity = UserInfo.builder()
                .url("url")
                .name("name")
                .login("userId")
                .email("email")
                .avatar_url("avatar")
                .location("location")
                .created_at(createdAt)
                .build();

        UserRepo repo = UserRepo.builder()
                .html_url("url")
                .name("name")
                .build();

        when(gitService.getUserInfoAsync("userId")).thenReturn(Mono.just(userInfoEntity));
        when(gitService.getUserReposAsync("userId")).thenReturn(Mono.just(new UserRepo[] {repo}));

        webTestClient.get()
                .uri("/usergit/userId")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserGitRepoResponse.class).isEqualTo(UserGitRepoResponse.builder()
                        .user_name("userId")
                        .display_name("name")
                        .avatar("avatar")
                        .geo_location("location")
                        .url("url")
                        .email("email")
                        .created_at(createdAt)
                        .repos(List.of(RepoDto.builder()
                                .url("url")
                                .name("name")
                                .build()))
                        .build());
    }

    @Test
    public void validateInvalidUserIdtest() {
        webTestClient.get()
                .uri("/usergit/user!")
                .exchange()
                .expectStatus()
                .is4xxClientError();
    }
}
