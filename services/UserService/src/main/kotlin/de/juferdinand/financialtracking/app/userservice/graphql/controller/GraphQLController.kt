package de.juferdinand.financialtracking.app.userservice.graphql.controller

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import de.juferdinand.financialtracking.app.userservice.graphql.service.UserService
import de.juferdinand.financialtracking.app.userservice.graphql.response.RequestResponse
import org.springframework.web.bind.annotation.CookieValue
import reactor.core.publisher.Mono

@DgsComponent
class GraphQLController(private val userService: UserService) {

    @DgsMutation(field = "changeUserInformation")
    fun changeUserInformation(
        @InputArgument userId: String,
        @InputArgument firstname: String,
        @InputArgument surname: String,
        @InputArgument email: String,
        @CookieValue access: String
    ): Mono<RequestResponse> {
        return userService.changeUserInformation(userId, firstname, surname, email, access)
    }

    fun deleteUser(
        @InputArgument userId: String,
        @CookieValue access: String
    ): Mono<RequestResponse> {
        return userService.deleteUser(userId, access)
    }
}