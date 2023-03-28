package com.example.demo.sao;

import com.example.demo.entity.UserInfoEntity;
import com.example.demo.entity.UserRepoEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GitService {

    private final WebClient webClient;

    private final String BASE_URL = "https://api.github.com/users";
    private final String baseUrl;

    @Autowired
    public GitService(WebClient webClient) {
        this.webClient = webClient;
        this.baseUrl = BASE_URL;
    }

    //For testing
    public GitService(WebClient webClient, String baseUrl) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
    }

    public Mono<UserInfoEntity> getUserInfoAsync(@NonNull String userId) {
        log.info("getting userInfo for {}", userId);
        return webClient.get()
                .uri(baseUrl + "/{userId}", userId)
                .retrieve()
                .bodyToMono(UserInfoEntity.class);
    }

    public Mono<UserRepoEntity[]> getUserReposAsync(@NonNull String userId) {
        log.info("getting repo for {}", userId);
        return webClient.get()
                .uri(baseUrl + "/{userId}/repos", userId)
                .retrieve()
                .bodyToMono(UserRepoEntity[].class);
    }
}
