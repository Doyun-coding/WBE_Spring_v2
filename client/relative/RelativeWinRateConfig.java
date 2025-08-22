package lolpago.client.relative;

import lolpago.common.exception.type.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

import static lolpago.client.HttpInterfaceUtil.createHttpInterface;
import static lolpago.common.exception.ExceptionMessage.EXTERNAL_API_EXCEPTION_MESSAGE;

@Configuration
public class RelativeWinRateConfig {

    @Value("${relativeWinRate-url}")
    private String relativeWinRateUrl;

    @Bean
    public RelativeWinRateApiClient relativeWinRateApiClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(relativeWinRateUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String responseData = new String(response.getBody().readAllBytes());
                    throw new ExternalApiException(EXTERNAL_API_EXCEPTION_MESSAGE.format(responseData), response.getStatusCode().value());
                })
                .build();
        return createHttpInterface(restClient, RelativeWinRateApiClient.class);
    }
}
