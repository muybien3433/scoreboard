package pl.muybien.model;

import java.time.LocalDateTime;

public class Match {
    private final String homeTeam;
    private final String awayTeam;
    private int homeScore;
    private int awayScore;
    private final LocalDateTime startTime;

    public Match(String homeTeam, String awayTeam, LocalDateTime startTime) {
        this(homeTeam, awayTeam, 0, 0, startTime);
    }

    public Match(String homeTeam, String awayTeam, int homeScore, int awayScore, LocalDateTime startTime) {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("homeTeam is null or empty");
        }

        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("awayTeam is null or empty");
        }

        if (homeScore < 0) {
            throw new IllegalArgumentException("homeScore is negative");
        }

        if (awayScore < 0) {
            throw new IllegalArgumentException("awayScore is negative");
        }

        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("Home and away team cannot be the same");
        }

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.startTime = startTime;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        validateScore(homeScore, "home");
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        validateScore(awayScore, "away");
        this.awayScore = awayScore;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return String.format("%s %d - %s %d", homeTeam, homeScore, awayTeam, awayScore);
    }

    private void validateScore(int score, String type) {
        if (score < 0) throw new IllegalArgumentException(type + "Score cannot be negative");
    }
}
