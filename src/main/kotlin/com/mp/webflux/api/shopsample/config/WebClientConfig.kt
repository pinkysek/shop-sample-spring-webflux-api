package com.mp.webflux.api.shopsample.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfig {

    @Bean
    fun imageWebClient(): WebClient {
        val httpClient = reactor.netty.http.client.HttpClient.create()
            .followRedirect(true)

        return WebClient.builder()
            .baseUrl("https://picsum.photos")
            .defaultHeaders { it.accept = listOf(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG) }
            .codecs { it.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) }
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}