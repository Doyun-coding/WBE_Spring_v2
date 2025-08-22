package lolpago.client.riot;

import lolpago.common.exception.type.ExternalApiException;
import lolpago.region.Region;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

import static lolpago.client.HttpInterfaceUtil.createHttpInterface;
import static lolpago.common.exception.ExceptionMessage.EXTERNAL_API_EXCEPTION_MESSAGE;

public class RiotClientFactory {

    private final Function<Region, String> urlProvider;

    public RiotClientFactory(Function<Region, String> urlProvider) {
        this.urlProvider = urlProvider;
    }

    public RiotApiClient createRiotClient(Region region) {
        String baseUrl = urlProvider.apply(region);
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION_MESSAGE.format(responseData), response.getStatusCode().value());
                })
                .build();
        return createHttpInterface(restClient, RiotApiClient.class);
    }
}
