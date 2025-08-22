package lolpago.client.datadragon;

import lolpago.common.exception.type.ExternalApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import static lolpago.client.HttpInterfaceUtil.createHttpInterface;
import static lolpago.common.exception.ExceptionMessage.EXTERNAL_API_EXCEPTION_MESSAGE;

@Configuration
public class DataDragonConfig {

    @Bean
    public DataDragonApiClient createDataDragonClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl("https://ddragon.leagueoflegends.com")
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION_MESSAGE.format(responseData), response.getStatusCode().value());
                })
                .build();
        return createHttpInterface(restClient, DataDragonApiClient.class);
    }
}
