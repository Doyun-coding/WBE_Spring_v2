package lolpago.client.datadragon;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DataDragonClient {

    private final DataDragonConfig dataDragonConfig;


    public Map<String, Object> getItemData(String dDragonVersion) {
        return dataDragonConfig.createDataDragonClient().fetchItemData(dDragonVersion);
    }

    public Map<String, Object> getChampionData(String dDragonVersion) {
        return dataDragonConfig.createDataDragonClient().fetchChampionData(dDragonVersion);
    }

    public Map<String, Object> getSpellData(String dDragonVersion) {
        return dataDragonConfig.createDataDragonClient().fetchSpellData(dDragonVersion);
    }

    public Map<String, Object> getChampionInfo(String dDragonVersion, String championName) {
        return dataDragonConfig.createDataDragonClient().fetchChampionInfo(dDragonVersion, championName);
    }

    public List<Map<String, Object>> getRuneData(String dDragonVersion) {
        return dataDragonConfig.createDataDragonClient().fetchRuneData(dDragonVersion);
    }
}
