package lolpago.client.riot;

import lolpago.client.riot.dto.LeagueEntryApiResponse;
import lolpago.client.riot.dto.SummonerBaseInfoApiResponse;
import lolpago.client.riot.dto.SummonerInfoApiResponse;
import lolpago.matchinfo.application.dto.MatchDetailApiResponse;
import lolpago.matchinfo.application.dto.MatchTimelineApiResponse;
import lolpago.spell.application.response.SpectatorCurrentGameInfoApiResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface RiotApiClient {

    @GetExchange(url = "/lol/league/v4/entries/by-puuid/{puuid}")
    List<LeagueEntryApiResponse> fetchLeagueEntries(
            @PathVariable("puuid") String puuid,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/lol/match/v5/matches/by-puuid/{puuid}/ids")
    List<String> fetchMatches(
            @PathVariable("puuid") String puuid,
            @RequestParam(name = "count") Integer count,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/lol/match/v5/matches/{matchId}")
    MatchDetailApiResponse fetchMatchInfo(
            @PathVariable("matchId") String matchId,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/lol/match/v5/matches/{matchId}/timeline")
    MatchTimelineApiResponse fetchMatchTimeline(
            @PathVariable("matchId") String matchId,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/riot/account/v1/accounts/by-riot-id/{summonerName}/{summonerTag}")
    SummonerBaseInfoApiResponse fetchSummonerBaseInfoFromNameAndTag(
            @PathVariable("summonerName") String summonerName,
            @PathVariable("summonerTag") String summonerTag,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/lol/summoner/v4/summoners/by-puuid/{puuid}")
    SummonerInfoApiResponse fetchSummonerInfo(
            @PathVariable("puuid") String summonerPuuid,
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/lol/platform/v3/champion-rotations")
    RotationChampionApiResponse fetchRotationChampions(
            @RequestParam("api_key") String apiKey
    );

    @GetExchange(url = "/riot/account/v1/accounts/by-puuid/{puuid}")
    SummonerBaseInfoApiResponse fetchSummonerBaseInfoFromPuuid(
            @PathVariable("puuid") String puuid,
            @RequestParam("api_key") String primaryApiKey
    );

    @GetExchange(url = "/lol/spectator/v5/active-games/by-summoner/{encryptedPUUID}")
    ResponseEntity<SpectatorCurrentGameInfoApiResponse> fetchSpectatorCurrentGameInfo(
        @PathVariable("encryptedPUUID") String encryptedPUUID,
        @RequestParam("api_key") String primaryApiKey
    );

}
