package de.juferdinand.financialtracking.app.authenticationservice.graphql.controller

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument
import com.netflix.graphql.dgs.context.DgsContext
import com.netflix.graphql.dgs.context.DgsContext.Companion.getCustomContext
import com.netflix.graphql.dgs.context.ReactiveDgsContext
import com.netflix.graphql.dgs.internal.DgsRequestData
import com.netflix.graphql.dgs.internal.DgsWebMvcRequestData
import com.netflix.graphql.dgs.reactive.internal.DgsReactiveRequestData
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.service.AuthService
import de.juferdinand.financialtracking.app.authenticationservice.graphql.service.PasswordResetService
import graphql.schema.DataFetchingEnvironment
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@DgsComponent
class GraphQLController(private val authService: AuthService, private val passwordResetService: PasswordResetService) {

    @DgsMutation
    fun registerUser(
        @InputArgument email: String,
        @InputArgument password: String,
        @InputArgument firstname: String,
        @InputArgument surname: String,
    ): Mono<RequestResponse> {
        return authService.registerUser(email, password, firstname, surname)
    }

    @DgsQuery
    fun loginUser(
        @InputArgument email: String,
        @InputArgument password: String,
        dfe: DataFetchingEnvironment
    ): Mono<RequestResponse> {
        return authService.loginUser(email, password, toServerHttpResponse(dfe))
    }

    @DgsQuery
    fun logoutUser(
        dfe: DataFetchingEnvironment,
        @CookieValue refresh: String
    ): Mono<RequestResponse> {
        return authService.logoutUser(toServerHttpRequest(dfe),toServerHttpResponse(dfe), refresh)
    }

    @DgsMutation
    fun verifyUser(
        @InputArgument token: String
    ): Mono<RequestResponse> {
        return authService.verifyUser(token)
    }

    @DgsQuery
    fun refreshToken(
        dfe: DataFetchingEnvironment,
        @CookieValue refresh: String
    ): Mono<RequestResponse> {
        return authService.refreshToken(toServerHttpResponse(dfe), refresh)
    }

    @DgsMutation
    fun revokeToken(
        dfe: DataFetchingEnvironment,
        @CookieValue refresh: String
    ): Mono<RequestResponse> {
        return authService.revokeToken(toServerHttpResponse(dfe), refresh)
    }

    @DgsQuery
    fun requestPasswordReset(
        email: String
    ): Mono<RequestResponse> {
        return passwordResetService.requestPasswordReset(email)
    }

    @DgsQuery
    fun verifyTokenForPasswordReset(
        token: String
    ): Mono<RequestResponse> {
        return passwordResetService.verifyTokenForPasswordReset(token)
    }

    @DgsMutation
    fun resetPassword(
        token: String,
        password: String
    ): Mono<RequestResponse> {
        return passwordResetService.resetPassword(token, password)
    }

    private fun toServerHttpResponse(dfe: DataFetchingEnvironment): ServerHttpResponse {
        return (ReactiveDgsContext.from(dfe)!!.requestData as DgsReactiveRequestData).serverRequest!!.exchange().response
    }
    private fun toServerHttpRequest(dfe: DataFetchingEnvironment): ServerRequest {
        return (ReactiveDgsContext.from(dfe)!!.requestData as DgsReactiveRequestData).serverRequest!!
    }
}