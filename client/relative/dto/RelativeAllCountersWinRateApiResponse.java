package lolpago.client.relative.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelativeAllCountersWinRateApiResponse {
    private final Map<String, CounterData> dataMap = new HashMap<>();

    @JsonAnySetter
    public void add(String key, CounterData value) {
        value.koreanName = key;
        dataMap.put(key, value);
    }

    public List<CounterData> getData() {
        return dataMap.values().stream()
                .filter(CounterData::isValidData)
                .collect(Collectors.toList());
    }

    @Getter
    public static class CounterData {
        private String koreanName;

        @JsonProperty("champion")
        private String champion;

        @JsonProperty("division")
        private String division;

        @JsonProperty("image")
        private String image;

        @JsonProperty("n_total_matches")
        private int nTotalMatches;

        @JsonProperty("patch_version")
        private String patchVersion;

        @JsonProperty("tier")
        private String tier;

        @JsonProperty("win_rate1")
        private double winRate1;

        @JsonProperty("win_rate2")
        private double winRate2;

        public boolean isValidData() {
            return nTotalMatches != -1 && winRate1 != -1 && winRate2 != -1;
        }
    }
}
