package com.example.demo.controller;

import com.example.demo.dto.RepoDto;
import com.example.demo.dto.UserGitRepoResponse;
import com.example.demo.entity.UserInfo;
import com.example.demo.entity.UserRepo;
import com.example.demo.service.GitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserGitController {

    private final GitService gitService;

    @Autowired
    public UserGitController(GitService gitService) {
        this.gitService = gitService;
    }

    @GetMapping("/usergit/{user_id}")
    public Mono<UserGitRepoResponse> getUserGitRepo(@PathVariable(value = "user_id") String userId) {

        //Calling userInfo and userRepo in parallel
        Mono<UserInfo> m1 = gitService.getUserInfoAsync(userId);
        Mono<UserRepo[]> m2 = gitService.getUserReposAsync(userId);

        return Mono.zip(m1, m2).flatMap(tuple -> {
            UserInfo userInfo = tuple.getT1();
            UserRepo[] userRepos = tuple.getT2();
            return Mono.just(buildResponse(userInfo, List.of(userRepos)));
        });
    }

    private UserGitRepoResponse buildResponse(UserInfo userInfo, List<UserRepo> repos) {
        return UserGitRepoResponse.builder()
                .user_name(userInfo.getLogin())
                .display_name(userInfo.getName())
                .avatar(userInfo.getAvatar_url())
                .geo_location(userInfo.getLocation())
                .email(userInfo.getEmail())
                .url(userInfo.getUrl())
                .created_at(userInfo.getCreated_at())
                .repos(repos.stream().map(repo -> RepoDto.builder()
                        .name(repo.getName())
                        .url(repo.getHtml_url())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
