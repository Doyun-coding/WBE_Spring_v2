package lolpago.client.riot;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Getter
@ConfigurationProperties(prefix = "riot")
public class RiotConfig {
    private final Api api;
    private final Current current;
    private final Urls urls;

    public RiotConfig(Api api, Current current, Urls urls) {
        this.api = api;
        this.current = current;
        this.urls = urls;
    }

    public record Api(Map<String, String> keys) {
    }

    public record Current(String season, String patchVersion, String ddragonVersion) {
    }

    public record Urls(String profileIconBase, Map<String, String> regions) {
    }
}
