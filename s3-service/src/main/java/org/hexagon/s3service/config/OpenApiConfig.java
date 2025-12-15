package org.hexagon.s3service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        servers = {
                @Server(url = "http://localhost:8000", description = "Gateway URL")
        }
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("S3 모듈의 API")
                .version("v1.0.0")
                .description("S3 모듈의 API 명세서입니다.");

        return new OpenAPI()
                .components(new Components())
                .info(info);
    }
}
