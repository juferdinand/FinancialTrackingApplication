package de.juferdinand.financialtracking.app.authenticationservice.graphql.controller

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.InputArgument
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.service.AuthService
import reactor.core.publisher.Mono

@DgsComponent
class GraphQLController(private val authService: AuthService) {

    fun registerUser(
        @InputArgument email: String,
        @InputArgument password: String,
        @InputArgument firstname: String,
        @InputArgument surname: String,
        @InputArgument birth: String
    ): Mono<RequestResponse> {
        return authService.registerUser(email, password, firstname, surname, birth)
    }

}