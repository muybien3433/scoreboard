import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.muybien.model.Match;
import pl.muybien.service.ScoreBoard;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreBoardTest {
    private ScoreBoard scoreBoard;

    @BeforeEach
    public void setUp() {
        scoreBoard = new ScoreBoard();
    }

    @Test
    void startMatch_shouldInitializeMatchAndScore_defaultScore() {
        scoreBoard.startMatch("Mexico", "Canada");
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals("Mexico", match.getHomeTeam());
        assertEquals("Canada", match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
    }

    @Test
    void startMatch_shouldInitializeMatchAndScore_intendedScoreAndTime() {
        LocalDateTime time = LocalDateTime.now();
        scoreBoard.startMatch("Mexico", "Canada", 5, 7, time);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals("Mexico", match.getHomeTeam());
        assertEquals("Canada", match.getAwayTeam());
        assertEquals(5, match.getHomeScore());
        assertEquals(7, match.getAwayScore());
    }

    @Test
    void startMatch_shouldRenameIfCaseTypoInTeamName() {
        scoreBoard.startMatch("geRmanY", "PoLAnD");
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals("Germany", match.getHomeTeam());
        assertEquals("Poland", match.getAwayTeam());
    }

    @Test
    void startMatch_shouldRenameIfCaseTypoInTeamName_evenWithManyWordsPerTeam() {
        scoreBoard.startMatch("uNited sTates", "UniTed kIngDom");
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals("United States", match.getHomeTeam());
        assertEquals("United Kingdom", match.getAwayTeam());
    }

    @Test
    void startMatch_shouldThrowExceptionIfTeamNameIsNull() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(null, "Poland"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch("Germany", null));
    }

    @Test
    void startMatch_shouldThrowExceptionIfTeamNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch("", "Poland"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch("Germany", ""));
    }

    @Test
    void startMatch_shouldThrowExceptionIfMatchAlreadyExist() {
        scoreBoard.startMatch("Mexico", "Canada");

        assertThrows(IllegalStateException.class,
                () -> scoreBoard.startMatch("Mexico", "Canada")
        );
    }

    @Test
    void startMatch_shouldThrowExceptionIfTeamNamesAreTheSame() {
        assertThrows(IllegalArgumentException.class,
                () -> scoreBoard.startMatch("Mexico", "Mexico")
        );
    }

    @Test
    void startMatch_shouldThrowExceptionIfMatchAlreadyExist_caseTypoInTeamName() {
        scoreBoard.startMatch("Mexico", "Canada");

        assertThrows(IllegalStateException.class,
                () -> scoreBoard.startMatch("meXico", "canadA")
        );
    }

    @Test
    void finishMatch_shouldFinishMatch() {
        scoreBoard.startMatch("Germany", "Poland");
        scoreBoard.finishMatch("Germany", "Poland");

        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void finishMatch_shouldFinishMatch_caseTypoInTeamName() {
        scoreBoard.startMatch("Germany", "Poland");
        scoreBoard.finishMatch("geRMaNy", "PolAND");

        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void finishMatch_shouldThrowExceptionIfMatchNotExist() {
        assertThrows(IllegalStateException.class,
                () -> scoreBoard.finishMatch("Germany", "Poland")
        );
    }

    @Test
    void updateScore_shouldUpdateMatchScore() {
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 3, 1);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(3, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
    }

    @Test
    void updateScore_shouldUpdateMatchScore_caseTypoInTeamName() {
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("SPaIn", "BrAzIl", 3, 1);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(3, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
    }

    @Test
    void updateScore_shouldAllowUpdateToZero() {
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 1, 0);
        scoreBoard.updateScore("Spain", "Brazil", 0, 0);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
    }

    @Test
    void updateScore_shouldAllowSameScore() {
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 1, 0);
        scoreBoard.updateScore("Spain", "Brazil", 1, 1);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(1, match.getHomeScore());
        assertEquals(1, match.getAwayScore());
    }

    @Test
    void updateScore_shouldAllowUpdateToLowerScore() {
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);
        scoreBoard.updateScore("Spain", "Brazil", 9, 2);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(9, match.getHomeScore());
        assertEquals(2, match.getAwayScore());
    }

    @Test
    void updateScore_shouldAllowHighScores() {
        scoreBoard.startMatch("Norway", "Sweden");
        scoreBoard.updateScore("Norway", "Sweden", 100, 200);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(100, match.getHomeScore());
        assertEquals(200, match.getAwayScore());
    }

    @Test
    void updateScore_shouldThrowExceptionIfMatchNotExist() {
        assertThrows(IllegalStateException.class,
                () -> scoreBoard.updateScore("Poland", "Germany", 1, 0)
        );
    }

    @Test
    void updateScore_shouldThrowExceptionIfBothScoresAreNegative() {
        assertThrows(IllegalStateException.class,
                () -> scoreBoard.updateScore("Poland", "Germany", -1, -10)
        );
    }

    @Test
    void updateScore_shouldThrowExceptionIfOneScoreIsNegative() {
        assertThrows(IllegalStateException.class,
                () -> scoreBoard.updateScore("Poland", "Germany", -2, 5)
        );
    }

    @Test
    void getSummary_shouldReturnListOfMatches() {
        scoreBoard.startMatch("Germany", "Poland");
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.startMatch("England", "France");

        assertEquals(3, scoreBoard.getSummary().size());
    }

    @Test
    void getSummary_shouldSortByTotalScoreAndRecency() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", 0, 5);

        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);

        scoreBoard.startMatch("Germany", "France");
        scoreBoard.updateScore("Germany", "France", 2, 2);

        scoreBoard.startMatch("Uruguay", "Italy");
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);

        scoreBoard.startMatch("Argentina", "Australia");
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals("Uruguay", summary.get(0).getHomeTeam());
        assertEquals("Italy", summary.get(0).getAwayTeam());

        assertEquals("Spain", summary.get(1).getHomeTeam());
        assertEquals("Brazil", summary.get(1).getAwayTeam());

        assertEquals("Mexico", summary.get(2).getHomeTeam());
        assertEquals("Canada", summary.get(2).getAwayTeam());

        assertEquals("Argentina", summary.get(3).getHomeTeam());
        assertEquals("Australia", summary.get(3).getAwayTeam());

        assertEquals("Germany", summary.get(4).getHomeTeam());
        assertEquals("France", summary.get(4).getAwayTeam());
    }

    @Test
    void getSummary_matchesWithSameTotalScore_shouldBeSortedByMostRecent() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowMinusOneSecond = now.minusSeconds(1);
        scoreBoard.startMatch("A", "B", 3, 3, nowMinusOneSecond);
        scoreBoard.startMatch("C", "D", 4, 2, now);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals("C", summary.get(0).getHomeTeam());
        assertEquals("D", summary.get(0).getAwayTeam());

        assertEquals("A", summary.get(1).getHomeTeam());
        assertEquals("B", summary.get(1).getAwayTeam());
    }

    @Test
    void getSummary_shouldBeEmptyIfNoMatchesFound() {
        assert(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void getSummary_shouldReorderAfterScoreUpdate() {
        scoreBoard.startMatch("Mexico", "Canada");
        scoreBoard.startMatch("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 5, 0);
        scoreBoard.updateScore("Mexico", "Canada", 10, 0);

        List<Match> updatedSummary = scoreBoard.getSummary();

        assertEquals("Mexico", updatedSummary.get(0).getHomeTeam());
        assertEquals("Canada", updatedSummary.get(0).getAwayTeam());

        assertEquals("Spain", updatedSummary.get(1).getHomeTeam());
        assertEquals("Brazil", updatedSummary.get(1).getAwayTeam());
    }
}