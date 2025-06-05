import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import pl.muybien.dto.MatchDTO;
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
        MatchDTO match = scoreBoard.getSummary().getFirst();

        assertEquals(inputHome, match.homeTeam());
        assertEquals(inputAway, match.awayTeam());
        assertEquals(0, match.homeScore());
        assertEquals(0, match.awayScore());
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
        MatchDTO match = scoreBoard.getSummary().getFirst();

        assertEquals(inputHome, match.homeTeam());
        assertEquals(inputAway, match.awayTeam());
        assertEquals(5, match.homeScore());
        assertEquals(7, match.awayScore());
        assertEquals(time, match.startTime());
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
        MatchDTO match = scoreBoard.getSummary().getFirst();

        assertEquals(expectedHome, match.homeTeam());
        assertEquals(expectedAway, match.awayTeam());
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

        MatchDTO match = scoreBoard.getSummary().getFirst();
        assertEquals(inputHomeScore, match.homeScore());
        assertEquals(inputAwayScore, match.awayScore());
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

        List<MatchDTO> summary = scoreBoard.getSummary();

        assertEquals("Uruguay", summary.get(0).homeTeam());
        assertEquals("Italy", summary.get(0).awayTeam());

        assertEquals("Spain", summary.get(1).homeTeam());
        assertEquals("Brazil", summary.get(1).awayTeam());

        assertEquals("Mexico", summary.get(2).homeTeam());
        assertEquals("Canada", summary.get(2).awayTeam());

        assertEquals("Argentina", summary.get(3).homeTeam());
        assertEquals("Australia", summary.get(3).awayTeam());

        assertEquals("Germany", summary.get(4).homeTeam());
        assertEquals("France", summary.get(4).awayTeam());
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

        List<MatchDTO> summary = scoreBoard.getSummary();

        assertEquals("Uruguay", summary.get(0).homeTeam());     // 12
        assertEquals("Spain", summary.get(1).homeTeam());       // 12 (later than Uruguay)
        assertEquals("Mexico", summary.get(2).homeTeam());      // 5
        assertEquals("Argentina", summary.get(3).homeTeam());   // 4
        assertEquals("Germany", summary.get(4).homeTeam());     // 4 (earlier than Argentina)
    }
}