package pe.test.exchange.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "pe.test.exchange.infrastructure.config")
public class FeignConfig {
}
