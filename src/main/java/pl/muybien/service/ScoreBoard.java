package pl.muybien.service;

import pl.muybien.model.Match;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {
    private final Map<String, Match> matches = new LinkedHashMap<>();

    public void startMatch(String homeTeam, String awayTeam) {
        NormalizedTeams nTeams = normalizeTeams(homeTeam, awayTeam);
        if (matches.containsKey(nTeams.key)) {
            throw new IllegalStateException("Match between %s - %s already exists".formatted(nTeams.home, nTeams.away));
        }

        matches.put(nTeams.key, new Match(nTeams.home, nTeams.away, LocalDateTime.now()));
    }

    public void startMatch(String homeTeam, String awayTeam, int homeScore, int awayScore, LocalDateTime startTime) {
        NormalizedTeams nTeams = normalizeTeams(homeTeam, awayTeam);
        if (matches.containsKey(nTeams.key)) {
            throw new IllegalStateException("Match already exists between " + homeTeam + " and " + awayTeam);
        }

        matches.put(nTeams.key, new Match(nTeams.home, nTeams.away, homeScore, awayScore, startTime));
    }

    public void finishMatch(String homeTeam, String awayTeam) {
        NormalizedTeams nTeams = normalizeTeams(homeTeam, awayTeam);
        if (!matches.containsKey(nTeams.key)) {
            throw new IllegalStateException("Match does not exist");
        }

        matches.remove(nTeams.key);
    }

    public void updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        NormalizedTeams nTeams = normalizeTeams(homeTeam, awayTeam);
        if (!matches.containsKey(nTeams.key)) {
            throw new IllegalStateException("Match does not exist");
        }

        Match match = matches.get(nTeams.key);

        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
    }

    public List<Match> getSummary() {
        return matches.values().stream()
                .sorted(Comparator
                        .comparingInt((Match m) -> m.getHomeScore() + m.getAwayScore())
                        .reversed()
                        .thenComparing(Match::getStartTime, Comparator.reverseOrder()))
                .toList();
    }

    private String normalizeTeamName(String teamName) {
        if (teamName == null) {
            throw new IllegalArgumentException("Team name cannot be null");
        }

        if (teamName.isBlank()) {
            throw new IllegalArgumentException("Team name cannot be blank");
        }

        String trimmed = teamName.trim();
        String[] words = trimmed.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase())
                    .append(" ");
        }

        return result.toString().trim();
    }

    private String generateMatchKey(String homeTeam, String awayTeam) {
        return homeTeam + "_" + awayTeam;
    }

    private record NormalizedTeams(String home, String away, String key) {}
    private NormalizedTeams normalizeTeams(String homeTeam, String awayTeam) {
        String normalizedHome = normalizeTeamName(homeTeam);
        String normalizedAway = normalizeTeamName(awayTeam);
        String key = generateMatchKey(normalizedHome, normalizedAway);

        return new NormalizedTeams(normalizedHome, normalizedAway, key);
    }
}