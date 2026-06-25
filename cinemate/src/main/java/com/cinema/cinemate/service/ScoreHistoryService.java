package com.cinema.cinemate.service;

import com.cinema.cinemate.entity.ScoreHistory;
import com.cinema.cinemate.entity.User;
import com.cinema.cinemate.enums.ErrorCode;
import com.cinema.cinemate.exception.AppException;
import com.cinema.cinemate.repository.ScoreHistoryRepository;
import com.cinema.cinemate.repository.UserRepository;
import com.cinema.cinemate.response.ScoreHistoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreHistoryService {

    private final ScoreHistoryRepository scoreHistoryRepository;
    private final UserRepository userRepository;

    public List<ScoreHistoryResponse> getMyScoreHistory(UUID userId, String fromDateStr, String toDateStr, String type) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        LocalDate fromDate = parseLocalDate(fromDateStr);
        LocalDate toDate = parseLocalDate(toDateStr);

        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }

        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.atTime(23, 59, 59, 999999);

        List<ScoreHistory> histories = scoreHistoryRepository.findFilteredHistory(user, type, start, end);

        return histories.stream().map(h -> ScoreHistoryResponse.builder()
                .id(h.getId().toString())
                .type(h.getType())
                .amount(h.getAmount())
                .movieName(h.getMovieName())
                .date(h.getDate())
                .build()
        ).collect(Collectors.toList());
    }

    private LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string is empty");
        }
        try {
            return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e1) {
            try {
                return LocalDate.parse(dateStr.trim()); // Fallback to yyyy-MM-dd
            } catch (Exception e2) {
                throw new IllegalArgumentException("Invalid date format: " + dateStr + ". Expected DD/MM/YYYY or YYYY-MM-DD");
            }
        }
    }
}
