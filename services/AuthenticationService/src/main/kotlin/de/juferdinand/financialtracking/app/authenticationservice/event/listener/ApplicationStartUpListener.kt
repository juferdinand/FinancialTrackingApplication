package de.juferdinand.financialtracking.app.authenticationservice.event.listener

import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import de.juferdinand.financialtracking.app.authenticationservice.event.exception.InvalidJWTConfigurationException
import de.juferdinand.financialtracking.app.authenticationservice.graphql.config.SpringJWTConfiguration
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ApplicationStartUpListener(private val springJWTConfiguration: SpringJWTConfiguration) {

    @EventListener(ApplicationReadyEvent::class)
    fun startSetupCheck() {
        checkJWTConfiguration()
        checkMailTemplates()
    }

    private fun checkMailTemplates() {
        if (this::class.java.getResourceAsStream("/mail/${TokenType.EMAIL_VERIFICATION.name.lowercase()}.html") == null) {
            throw RuntimeException("Mail template for email verification not found")
        }
        if (this::class.java.getResourceAsStream("/mail/${TokenType.PASSWORD_RESET.name.lowercase()}.html") == null) {
            throw RuntimeException("Mail template for password reset not found")
        }
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