package lolpago.client.riot.dto;

import java.util.Optional;

public record LeagueEntryApiResponse(
        String leagueId,
        String summonerId,
        String queueType,
        String tier,
        String rank,
        Integer leaguePoints,
        Integer wins,
        Integer losses,
        Boolean hotStreak,
        Boolean veteran,
        Boolean freshBlood,
        Boolean inactive,
        Optional<MiniSeriesDTO> miniSeries
) {
    public record MiniSeriesDTO(
            Integer losses,
            String progress,
            Integer target,
            Integer wins
    ) {}
}
