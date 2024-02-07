package de.juferdinand.financialtracking.app.authenticationservice.event.exception

class InvalidJWTConfigurationException() :
    IllegalStateException("Invalid JWT configuration. Please check your configuration.") {

}