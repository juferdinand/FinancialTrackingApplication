package de.juferdinand.financialtracking.app.authenticationservice.graphql.exception

class InvalidJWTConfigurationException() :
    IllegalStateException("Invalid JWT configuration. Please check your configuration.") {

}