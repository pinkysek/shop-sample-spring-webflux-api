package com.mp.webflux.api.shopsample.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun customOpenAPI(): OpenAPI? {
        return OpenAPI()
            .info(
                Info()
                    .title("Shop Sample Spring WebFlux API")
                    .version("1.0")
                    .description("API documentation for Shop Sample")
            )
    }
}