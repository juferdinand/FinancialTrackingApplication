package de.juferdinand.financialtracking.app.authenticationservice.graphql.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.server.ServerWebExchange

@Configuration
class WebConfig {

    @Bean
    fun corsFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.addAllowedOrigin("http://localhost:3000")
        corsConfig.addAllowedHeader("*")
        corsConfig.addAllowedMethod("POST")
        corsConfig.allowCredentials = true

        return CorsWebFilter { exchange: ServerWebExchange? -> corsConfig }
    }
}
