package de.juferdinand.financialtracking.app.authenticationservice.graphql.response

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class RequestResponse(
    val success: Boolean,
    val statusCode: String,
    val message: String,
    val timestamp: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))

)
