package com.cinema.cinemate.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeValidationResponse {
    private boolean valid;
    private List<String> hardErrors;
    private List<String> softWarnings;
}
