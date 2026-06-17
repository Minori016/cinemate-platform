package com.cinema.cinemate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieMediaResponse {
    private UUID id;
    private String mediaType;
    private String url;
    private String title;
}
