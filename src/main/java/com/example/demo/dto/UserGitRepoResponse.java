package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class UserGitRepoResponse {

    private String user_name;
    private String display_name;
    private String avatar;
    private String geo_location;
    private String email;
    private String url;
    private Instant created_at;
    private List<RepoDto> repos;
}

