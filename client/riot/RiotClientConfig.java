package lolpago.client.riot;

import lolpago.region.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EnumMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class RiotClientConfig {

    private final RiotConfig riotConfig;

    @Bean
    public RiotClientFactory riotClientFactory() {
        return new RiotClientFactory(region -> riotConfig.getUrls().regions().get(region.getKey()));
    }

    @Bean
    public Map<Region, RiotApiClient> regionRiotClientMapping(RiotClientFactory factory) {
        Map<Region, RiotApiClient> mapping = new EnumMap<>(Region.class);
        for (Region region : Region.values()) {
            mapping.put(region, factory.createRiotClient(region));
        }
        return mapping;
    }
}
