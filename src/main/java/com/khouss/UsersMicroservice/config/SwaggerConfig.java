package com.khouss.UsersMicroservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        OpenAPI api = new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title("Users Microservice API")
                        .description("API pour OMPay.")
                        .version("1.0.0")
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                );

        // Optionnel: config serveur Swagger depuis l'ENV SWAGGER_SERVER_URL (utile pour Render)
        String serverUrl = System.getenv("SWAGGER_SERVER_URL");
        if (serverUrl != null && !serverUrl.isBlank()) {
            api.addServersItem(new Server().url(serverUrl));
        }
        return api;
    }
}
