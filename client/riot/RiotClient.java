package lolpago.client.riot;

import lolpago.client.riot.dto.LeagueEntryApiResponse;
import lolpago.client.riot.dto.SummonerBaseInfoApiResponse;
import lolpago.client.riot.dto.SummonerInfoApiResponse;
import lolpago.matchinfo.application.dto.MatchDetailApiResponse;
import lolpago.matchinfo.application.dto.MatchTimelineApiResponse;
import lolpago.region.Region;
import lolpago.spell.application.response.SpectatorCurrentGameInfoApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@Configuration
public class RiotClient {

    private final RiotConfig riotConfig;
    private final Map<Region, RiotApiClient> regionRiotClientMapping;
    private final AtomicInteger currentKeyIndex = new AtomicInteger(0);
    private final List<String> apiKeys;


    public RiotClient(RiotConfig riotConfig, Map<Region, RiotApiClient> regionRiotClientMapping) {
        this.riotConfig = riotConfig;
        this.regionRiotClientMapping = regionRiotClientMapping;
        this.apiKeys = riotConfig.getApi().keys().keySet().stream().toList();
    }

    private String getApiKey() {
        return riotConfig.getApi().keys().get(apiKeys.get(currentKeyIndex.getAndUpdate(i -> (i + 1) % apiKeys.size())));
    }

    // puuid 관련 작업은 primary key를 사용해야 함
    private String getPrimaryApiKey() {
        return riotConfig.getApi().keys().get("primary");
    }

    private RiotApiClient getClient(Region region) {
        return regionRiotClientMapping.get(region);
    }

    public List<LeagueEntryApiResponse> getLeagueEntries(String puuid, Region region) {
        return getClient(region).fetchLeagueEntries(puuid, getPrimaryApiKey());
    }

    public List<String> getMatches(String puuid, Region region) {
        return getClient(region.getGroupRegion()).fetchMatches(puuid, 30, getPrimaryApiKey());
    }

    public MatchDetailApiResponse getMatchDetail(String matchId, Region region) {
        return getClient(region.getGroupRegion()).fetchMatchInfo(matchId, getPrimaryApiKey());
    }

    public MatchTimelineApiResponse getMatchTimeline(String matchId, Region region) {
        return getClient(region.getGroupRegion()).fetchMatchTimeline(matchId, getPrimaryApiKey());
    }

    public SummonerBaseInfoApiResponse getSummonerBaseInfoFromNameAndTag(String summonerName, String summonerTag, Region region) {
        return getClient(region.getGroupRegion()).fetchSummonerBaseInfoFromNameAndTag(summonerName, summonerTag, getPrimaryApiKey());
    }

    public SummonerBaseInfoApiResponse getSummonerBaseInfoFromPuuid(String puuid, Region region) {
        return getClient(region.getGroupRegion()).fetchSummonerBaseInfoFromPuuid(puuid, getPrimaryApiKey());
    }

    public SummonerInfoApiResponse getSummonerInfo(String puuid, Region region) {
        return getClient(region).fetchSummonerInfo(puuid, getPrimaryApiKey());
    }

    public RotationChampionApiResponse getRotationChampions(Region region) {
        return getClient(region).fetchRotationChampions(getApiKey());
    }

    public ResponseEntity<SpectatorCurrentGameInfoApiResponse> getSpectatorCurrentGameInfo(String encryptedPUUID, Region region) {
        return getClient(region).fetchSpectatorCurrentGameInfo(encryptedPUUID, getPrimaryApiKey());
    }

}
