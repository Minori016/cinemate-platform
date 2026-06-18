package com.cinema.cinemate.controller;

import com.cinema.cinemate.repository.CountryRepository;
import com.cinema.cinemate.response.ApiResponse;
import com.cinema.cinemate.response.CountryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryRepository countryRepository;

    @GetMapping
    public ApiResponse<List<CountryResponse>> getAllCountries() {
        List<CountryResponse> countries = countryRepository.findAll().stream()
                .map(c -> CountryResponse.builder()
                        .id(c.getId())
                        .code(c.getCode())
                        .name(c.getName())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<CountryResponse>>builder()
                .result(countries)
                .build();
    }
}
