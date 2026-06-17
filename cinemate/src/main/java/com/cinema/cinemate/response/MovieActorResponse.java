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
public class MovieActorResponse {
    private UUID actorId;
    private String fullName;
    private String avatarUrl;
    private String characterName;
}
