package de.juferdinand.financialtracking.app.authenticationservice.entity

import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse

data class GraphQLResponse(
    val data: Map<String, RequestResponse> = emptyMap(),
)
