package com.example.memberservice.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://hexagon.chlab.org", description = "Gateway URL")
    }
)
public class OpenApiConfig {

}
