package pl.muybien.service;

import pl.muybien.model.Match;

import java.time.LocalDateTime;
import java.util.List;

public class ScoreBoard {
    public void startMatch(String homeTeam, String awayTeam) {
    }

    public void startMatch(String homeTeam, String awayTeam, int homeScore, int awayScore, LocalDateTime startTime) {
    }

    public List<Match> getSummary() {
        return null;
    }

    public void removeMatch(String homeTeam, String awayTeam) {
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
    }
}
