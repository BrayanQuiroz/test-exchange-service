package pe.test.exchange.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pe.test.exchange.infrastructure.external.dto.ExchangeRateApiResponse;

@FeignClient(name = "exchangeRateClient", url = "${exchange-rate.api.base-url}")
public interface ExchangeRateFeignClient {

    @GetMapping("/{apiKey}/latest/{baseCode}")
    ExchangeRateApiResponse getLatestRates(
            @PathVariable("apiKey") String apiKey,
            @PathVariable("baseCode") String baseCode
    );
}