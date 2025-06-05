package pl.muybien.mapper;

import pl.muybien.dto.MatchDTO;
import pl.muybien.model.Match;

public class MatchDTOMapper {
    public static MatchDTO toDTO(Match match) {
        return new MatchDTO(
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getHomeScore(),
                match.getAwayScore(),
                match.getStartTime()
        );
    }
}