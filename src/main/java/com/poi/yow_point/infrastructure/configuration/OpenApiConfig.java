package com.poi.yow_point.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server prodServer = new Server();
        prodServer.setUrl("https://poi-navigoo.pynfi.com");
        prodServer.setDescription("Production server");

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local server");

        return new OpenAPI()
                .info(new Info()
                        .title("POI Navigoo API")
                        .version("1.0")
                        .description("API for Point of Interest navigation services."))
                .servers(List.of(prodServer, localServer));
    }
}
