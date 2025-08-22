package lolpago.client.relative.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record relativeWinRateApiResponse(
        @JsonProperty("champ1") String champ1,
        @JsonProperty("champ2") String champ2,
        @JsonProperty("image1") String image1,
        @JsonProperty("image2") String image2,
        @JsonProperty("n_total_matches") int nTotalMatches,
        @JsonProperty("win_rate1") double winRate1,
        @JsonProperty("win_rate2") double winRate2
) {
}
