package com.cinema.cinemate.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActorInput {

    @NotBlank(message = "Actor name is required")
    private String fullName;

    private String characterName;
}
