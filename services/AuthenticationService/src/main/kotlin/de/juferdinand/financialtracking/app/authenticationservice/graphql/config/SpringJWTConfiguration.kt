package de.juferdinand.financialtracking.app.authenticationservice.graphql.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
class SpringJWTConfiguration {

    lateinit var secrets: Map<String, String>

}