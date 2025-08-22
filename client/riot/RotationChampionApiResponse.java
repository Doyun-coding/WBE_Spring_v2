package lolpago.client.riot;

import java.util.List;

public record RotationChampionApiResponse(
        int maxNewPlayerLevel,
        List<Integer> freeChampionIdsForNewPlayers,
        List<Integer> freeChampionIds
) {
}
