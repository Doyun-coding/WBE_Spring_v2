package lolpago.client.relative;

import lolpago.client.relative.dto.RelativeAllCountersWinRateApiResponse;
import lolpago.client.relative.dto.relativeWinRateApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface RelativeWinRateApiClient {

    @GetExchange("/api/champions/{position}/{champ1}/counters")
    relativeWinRateApiResponse getRelativeWinRate(
            @PathVariable String position,
            @PathVariable String champ1,
            @RequestParam("target_champion") String targetChampion,
            @RequestParam("tier") String tier,
            @RequestParam("div") String div,
            @RequestParam("patch") String patch
    );

    @GetExchange("/api/champions/{position}/{champ1}/all_counters")
    RelativeAllCountersWinRateApiResponse getRelativeAllCountersWinRate(
            @PathVariable String position,
            @PathVariable String champ1,
            @RequestParam("tier") String tier,
            @RequestParam("div") String div,
            @RequestParam("patch") String patch
    );
}
