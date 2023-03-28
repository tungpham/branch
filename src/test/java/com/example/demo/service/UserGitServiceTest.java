package com.example.demo.service;

import com.example.demo.entity.UserInfoEntity;
import com.example.demo.entity.UserRepoEntity;
import com.example.demo.model.User;
import com.example.demo.sao.GitService;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserGitServiceTest {

    GitService sao = mock(GitService.class);
    UserGitService service = new UserGitService(sao);

    @Test
    public void testGetUser() {
        UserInfoEntity userInfoEntity = UserInfoEntity.builder()
                .url("url")
                .name("name")
                .login("userId")
                .email("email")
                .avatar_url("avatar")
                .location("location")
                .created_at(Instant.now())
                .build();

        UserRepoEntity repoEntity = UserRepoEntity.builder()
                .html_url("url")
                .name("name")
                .build();
        UserRepoEntity[] repo = new UserRepoEntity[] {repoEntity};

        when(sao.getUserInfoAsync("userId")).thenReturn(Mono.just(userInfoEntity));
        when(sao.getUserReposAsync("userId")).thenReturn(Mono.just(repo));

        Mono<User> user = service.getUser("userId");

        assertEquals("userId", user.block().getUserName());
        assertEquals("name", user.block().getDisplayName());
        assertEquals("url", user.block().getUrl());
        assertEquals("email", user.block().getEmail());
        assertEquals("avatar", user.block().getAvatar());
        assertEquals("location", user.block().getGeoLocation());
        assertEquals("url", user.block().getRepos().get(0).getUrl());
        assertEquals("name", user.block().getRepos().get(0).getName());
    }
}
