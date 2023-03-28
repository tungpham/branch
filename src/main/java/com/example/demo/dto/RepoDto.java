package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RepoDto {
    private String name;
    private String url;
}
