package de.juferdinand.financialtracking.app.authenticationservice.graphql.exception

class TokenCreationException : Exception {

    constructor() : super("Couldn't create unique token. Please try again.")

    constructor(message: String) : super(message)

}