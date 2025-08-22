package lolpago.client.riot.dto;

public record SummonerInfoApiResponse(
        String id,
        String accountId,
        String puuid,
        Integer profileIconId,
        Long revisionDate,
        Integer summonerLevel
) {
}
