package com.example.demo.controller;

import com.example.demo.dto.RepoDto;
import com.example.demo.dto.UserGitRepoResponse;
import com.example.demo.service.UserGitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserGitController {

    private final UserGitService userGitService;

    @Autowired
    public UserGitController(UserGitService userGitService) {
        this.userGitService = userGitService;
    }

    /**
     * Get userInfo and user repos.
     * @param userId - validated to make sure only a-zA-Z0-9. Anything else will be rejected with 404 right away
     * @return
     */
    @GetMapping("/usergit/{user_id:[a-zA-Z0-9]*}")
    public Mono<UserGitRepoResponse> getUserGitRepo(@PathVariable(value = "user_id") String userId) {

        return userGitService.getUser(userId).map(userInfo ->
            UserGitRepoResponse.builder()
                    .user_name(userInfo.getUserName())
                    .display_name(userInfo.getDisplayName())
                    .avatar(userInfo.getAvatar())
                    .geo_location(userInfo.getGeoLocation())
                    .email(userInfo.getEmail())
                    .url(userInfo.getUrl())
                    .created_at(userInfo.getCreatedAt())
                    .repos(userInfo.getRepos().stream().map(repo -> RepoDto.builder()
                            .name(repo.getName())
                            .url(repo.getUrl())
                            .build()).collect(Collectors.toList()))
                    .build());
    }
}
