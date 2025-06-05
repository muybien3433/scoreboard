import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @CsvSource({
            "Mexico, Canada",
            "Germany, Poland",
            "United States, United Kingdom",
            "Brazil, Argentina",
            "Central African Republic, Netherlands Antilles",
            "South Africa, Uganda"
    })
    void startMatch_shouldInitializeMatchAndScore(String inputHome, String inputAway) {
        scoreBoard.startMatch(inputHome, inputAway);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(inputHome, match.getHomeTeam());
        assertEquals(inputAway, match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
    }

    @ParameterizedTest
    @CsvSource({
            "Mexico, Canada",
            "Germany, Poland",
            "United States, United Kingdom",
            "Brazil, Argentina",
            "Central African Republic, Netherlands Antilles",
            "South Africa, Uganda"
    })
    void startMatch_shouldInitializeMatchAndScore_intendedScoreAndTime(String inputHome, String inputAway) {
        LocalDateTime time = LocalDateTime.now();
        scoreBoard.startMatch(inputHome, inputAway, 5, 7, time);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(inputHome, match.getHomeTeam());
        assertEquals(inputAway, match.getAwayTeam());
        assertEquals(5, match.getHomeScore());
        assertEquals(7, match.getAwayScore());
        assertEquals(time, match.getStartTime());
    }

    @ParameterizedTest
    @CsvSource({
            "uNiteD sTatEs, uNiTED kiNgDOM, United States, United Kingdom",
            "CenTRaL afRicAn rePUblIc, PapUA NEW GUiNeA, Central African Republic, Papua New Guinea",
            "neThERlanDS aNTiLLeS, solOmOn isLANds, Netherlands Antilles, Solomon Islands",
            "   uGanDA,   neW ZEALand  , Uganda, New Zealand",
            "brazil, argentina, Brazil, Argentina",
            "GERMANY, FRANCE, Germany, France",
            "iTaLy, sOuTh AfRiCa, Italy, South Africa",
            "esPañA, méXico, España, México"
    })
    void startMatch_shouldNormalizeTeamName(String inputHome, String inputAway, String expectedHome, String expectedAway) {
        scoreBoard.startMatch(inputHome, inputAway);
        Match match = scoreBoard.getSummary().getFirst();

        assertEquals(expectedHome, match.getHomeTeam());
        assertEquals(expectedAway, match.getAwayTeam());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'', Poland",
            "Germany, ''",
            "'', ''",
            "Germany, Germany",
            "GerMAny, GeRmaNY",
            "null, Germany",
            "null, null",
            "Germany, null"
    },
            nullValues = {"null"}
    )
    void startMatch_shouldThrowExceptionForInvalidTeamName(String inputHome, String inputAway) {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startMatch(inputHome, inputAway));
    }

    @Test
    void startMatch_shouldThrowExceptionIfMatchAlreadyExist() {
        scoreBoard.startMatch("Germany", "Poland");

        assertThrows(IllegalStateException.class,
                () -> scoreBoard.startMatch("Germany", "Poland")
        );
    }

    @Test
    void finishMatch_shouldRemoveMatchFromSummary() {
        scoreBoard.startMatch("Germany", "Poland");
        scoreBoard.finishMatch("Germany", "Poland");

        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    void finishMatch_shouldThrowExceptionIfMatchNotExist() {
        assertThrows(IllegalStateException.class, () -> scoreBoard.finishMatch("Germany", "Poland"));
    }

    @ParameterizedTest
    @CsvSource({
            "Germany, Poland, 1, 1",
            "Germany, Poland, 5, 0",
            "Germany, Poland, 0, 0",
            "Germany, Poland, 51, 121",
            "Germany, Poland, 5215125, 912149124"
    })
    void updateScore_shouldUpdateMatchScore(String inputHome, String inputAway, int inputHomeScore, int inputAwayScore) {
        scoreBoard.startMatch(inputHome, inputAway);
        scoreBoard.updateScore(inputHome, inputAway, inputHomeScore, inputAwayScore);

        Match match = scoreBoard.getSummary().getFirst();
        assertEquals(inputHomeScore, match.getHomeScore());
        assertEquals(inputAwayScore, match.getAwayScore());
    }

    @Test
    void updateScore_shouldThrowExceptionIfMatchNotExist() {
        assertThrows(IllegalStateException.class,
                () -> scoreBoard.updateScore("Poland", "Germany", 1, 0)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "Germany, Poland, -1, 1",
            "Germany, Poland, 1, -1",
            "Germany, Poland, -5, -7"
    })
    void updateScore_shouldThrowForNegativeScores(String home, String away, int homeScore, int awayScore) {
        scoreBoard.startMatch(home, away);
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore(home, away, homeScore, awayScore));
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
    void getSummary_shouldReturnMatchesSortedByTotalScoreAndStartTime() {
        scoreBoard.startMatch("Mexico", "Canada", 0, 0,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0, 1)
        );
        scoreBoard.startMatch("Spain", "Brazil", 0, 0,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0, 2)
        );
        scoreBoard.startMatch("Germany", "France", 0, 0,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0, 3)
        );
        scoreBoard.startMatch("Uruguay", "Italy", 0, 0,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0, 4)
        );
        scoreBoard.startMatch("Argentina", "Australia", 0, 0,
                LocalDateTime.of(2025, 1, 1, 10, 0, 0, 5)
        );

        scoreBoard.updateScore("Mexico", "Canada", 0, 5);
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);
        scoreBoard.updateScore("Germany", "France", 2, 2);
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);

        List<Match> summary = scoreBoard.getSummary();

        assertEquals("Uruguay", summary.get(0).getHomeTeam());     // 12
        assertEquals("Spain", summary.get(1).getHomeTeam());       // 12 (later than Uruguay)
        assertEquals("Mexico", summary.get(2).getHomeTeam());      // 5
        assertEquals("Argentina", summary.get(3).getHomeTeam());   // 4
        assertEquals("Germany", summary.get(4).getHomeTeam());     // 4 (earlier than Argentina)
    }
}