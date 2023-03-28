package com.example.demo.service;

import com.example.demo.entity.UserInfoEntity;
import com.example.demo.entity.UserRepoEntity;
import com.example.demo.model.User;
import com.example.demo.sao.GitService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGitService {

    private final GitService gitService;

    @Autowired
    public UserGitService(GitService gitService) {
        this.gitService = gitService;
    }

    public Mono<User> getUser(@NonNull String userId) {
        //Calling userInfo and userRepo in parallel
        Mono<UserInfoEntity> m1 = gitService.getUserInfoAsync(userId);
        Mono<UserRepoEntity[]> m2 = gitService.getUserReposAsync(userId);

        return Mono.zip(m1, m2).flatMap(tuple -> {
            UserInfoEntity userInfoEntity = tuple.getT1();
            UserRepoEntity[] userRepoEntities = tuple.getT2();
            return Mono.just(buildResponse(userInfoEntity, List.of(userRepoEntities)));
        });
    }

    private User buildResponse(UserInfoEntity userInfoEntity, List<UserRepoEntity> repos) {
        return User.builder()
                .userName(userInfoEntity.getLogin())
                .displayName(userInfoEntity.getName())
                .avatar(userInfoEntity.getAvatar_url())
                .geoLocation(userInfoEntity.getLocation())
                .email(userInfoEntity.getEmail())
                .url(userInfoEntity.getUrl())
                .createdAt(userInfoEntity.getCreated_at())
                .repos(repos.stream().map(repo -> com.example.demo.model.UserRepo.builder()
                        .name(repo.getName())
                        .url(repo.getHtml_url())
                        .build()).collect(Collectors.toList()))
                .build();
    }
}
