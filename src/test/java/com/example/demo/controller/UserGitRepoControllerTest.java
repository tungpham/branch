package com.example.demo.controller;

import com.example.demo.dto.RepoDto;
import com.example.demo.dto.UserGitRepoResponse;
import com.example.demo.model.User;
import com.example.demo.model.UserRepo;
import com.example.demo.service.UserGitService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebFluxTest(UserGitController.class)
public class UserGitRepoControllerTest {

    @MockBean
    private UserGitService userGitService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void getUserRepo() {
        Instant createdAt = Instant.now();
        User user = User.builder()
                .userName("userId")
                .displayName("name")
                .url("url")
                .email("email")
                .avatar("avatar")
                .geoLocation("location")
                .createdAt(createdAt)
                .repos(List.of(UserRepo.builder()
                        .url("url")
                        .name("name")
                        .build()))
                .build();

        when(userGitService.getUser("userId")).thenReturn(Mono.just(user));

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

    @Test
    public void whenDependencyReturnExceptionReturn500() {
        when(userGitService.getUser("userId")).thenThrow(WebClientResponseException.class);
        webTestClient.get()
                .uri("/usergit/user")
                .exchange()
                .expectStatus()
                .is5xxServerError();
    }
}
