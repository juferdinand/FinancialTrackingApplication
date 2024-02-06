package de.juferdinand.financialtracking.app.authenticationservice.graphql.controller

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext.Companion.getCustomContext
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.service.AuthService
import graphql.schema.DataFetchingEnvironment
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.server.ServerWebExchange
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

    fun loginUser(
        @InputArgument email: String,
        @InputArgument password: String,
        dfe: DataFetchingEnvironment
    ): Mono<RequestResponse> {
        return authService.loginUser(email, password, toServerHttpResponse(dfe))
    }

    fun refreshToken(dfe: DataFetchingEnvironment, @CookieValue refresh:String): Mono<RequestResponse> {
        return authService.refreshToken(toServerHttpResponse(dfe), refresh)
    }

    fun revokeToken(dfe: DataFetchingEnvironment, @CookieValue refresh:String): Mono<RequestResponse> {
        return authService.revokeToken(toServerHttpResponse(dfe), refresh)
    }

    private fun toServerHttpResponse(dfe: DataFetchingEnvironment): ServerHttpResponse {
        val exchange: ServerWebExchange = getCustomContext(dfe)
        return exchange.response
    }

}