package de.juferdinand.financialtracking.app.authenticationservice.event.listener

import de.juferdinand.financialtracking.app.authenticationservice.graphql.config.SpringJWTConfiguration
import de.juferdinand.financialtracking.app.authenticationservice.graphql.exception.InvalidJWTConfigurationException
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationStartUpListener(private val springJWTConfiguration: SpringJWTConfiguration) {

    @EventListener(ApplicationReadyEvent::class)
    fun startSetupCheck() {
        checkJWTConfiguration()
    }

    private fun checkJWTConfiguration() {
        if (springJWTConfiguration.secrets.isEmpty()) {
            throw InvalidJWTConfigurationException()
        }
        if (springJWTConfiguration.secrets["access"].isNullOrBlank() || springJWTConfiguration.secrets["refresh"].isNullOrBlank()) {
            throw InvalidJWTConfigurationException()
        }
    }
}