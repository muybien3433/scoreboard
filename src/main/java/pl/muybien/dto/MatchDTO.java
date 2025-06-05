package pl.muybien.dto;

import java.time.LocalDateTime;

public record MatchDTO(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore,
        LocalDateTime startTime
) {
}