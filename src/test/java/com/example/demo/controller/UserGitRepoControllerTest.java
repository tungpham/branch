package com.example.demo.controller;

import com.example.demo.dto.UserGitRepoResponse;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserRepo;
import com.example.demo.service.GitService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserGitRepoControllerTest {

    GitService gitService = mock(GitService.class);
    UserGitController controller = new UserGitController(gitService);

    @Test
    public void getUserRepo() {
        UserInfo userInfo = UserInfo.builder()
                .url("url")
                .name("name")
                .login("userId")
                .email("email")
                .avatar_url("avatar")
                .location("location")
                .created_at(Instant.now())
                .build();

        UserRepo repo = UserRepo.builder()
                .html_url("url")
                .name("name")
                .build();

        when(gitService.getUserInfoAsync("userId")).thenReturn(Mono.just(userInfo));
        when(gitService.getUserReposAsync("userId")).thenReturn(Mono.just(new UserRepo[] {repo}));

        Mono<UserGitRepoResponse> mResponse = controller.getUserGitRepo("userId");
        UserGitRepoResponse response = mResponse.block();

        assertEquals(userInfo.getLogin(), response.getUser_name());
        assertEquals(userInfo.getName(), response.getDisplay_name());
        assertEquals(userInfo.getUrl(), response.getUrl());
        assertEquals(userInfo.getAvatar_url(), response.getAvatar());
        assertEquals(userInfo.getLocation(), response.getGeo_location());
        assertEquals(userInfo.getEmail(), response.getEmail());
        assertEquals(userInfo.getCreated_at(), response.getCreated_at());

        assertEquals(repo.getName(), response.getRepos().get(0).getName());
        assertEquals(repo.getHtml_url(), response.getRepos().get(0).getUrl());
    }
}
