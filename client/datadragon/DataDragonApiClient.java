package lolpago.client.datadragon;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.Map;

public interface DataDragonApiClient {

    @GetExchange("/cdn/{version}/data/ko_KR/item.json")
    Map<String, Object> fetchItemData(@PathVariable("version") String version);

    @GetExchange("/cdn/{version}/data/ko_KR/champion.json")
    Map<String, Object> fetchChampionData(@PathVariable("version") String version);

    @GetExchange("/cdn/{version}/data/ko_KR/runesReforged.json")
    List<Map<String, Object>> fetchRuneData(@PathVariable("version") String version);

    @GetExchange("/cdn/{version}/data/ko_KR/summoner.json")
    Map<String, Object> fetchSpellData(@PathVariable("version") String version);

    @GetExchange("/cdn/{version}/data/ko_KR/champion/{championName}.json")
    Map<String, Object> fetchChampionInfo(
            @PathVariable("version") String version,
            @PathVariable("championName") String championName
    );
}
