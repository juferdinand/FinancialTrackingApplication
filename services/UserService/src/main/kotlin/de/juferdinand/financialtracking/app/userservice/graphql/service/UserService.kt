package de.juferdinand.financialtracking.app.userservice.graphql.service

import de.juferdinand.financialtracking.app.userservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.userservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.userservice.graphql.util.JWTUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtUtils: JWTUtils
) {
    fun changeUserInformation(
        userId: String,
        firstname: String,
        surname: String,
        email: String,
        access: String
    ): Mono<RequestResponse> {
        checkToken(access, userId)?.let {
            return Mono.just(it)
        }
        return userRepository.findUserByUserId(userId).flatMap {
            it.email = email
            it.firstname = firstname
            it.surname = surname
            userRepository.save(it).map {
                RequestResponse(
                    success = true,
                    statusCode = "0",
                    message = "User information changed"
                )
            }
        }
    }

    fun deleteUser(userId: String, access: String): Mono<RequestResponse> {
        checkToken(access, userId)?.let {
            return Mono.just(it)
        }
        return userRepository.deleteById(userId).then(Mono.fromCallable {
            RequestResponse(
                success = true,
                statusCode = "0",
                message = "User deleted"
            )
        })
    }

    fun checkToken(access: String, userId: String?): RequestResponse? {
        if (!jwtUtils.isTokenValid(access)) {
            return RequestResponse(
                success = false,
                statusCode = "4",
                message = "Token expired"
            )
        }
        if (userId != null && jwtUtils.getSubjectFromToken(access) != userId) {
            return RequestResponse(
                success = false,
                statusCode = "5",
                message = "Token does not match user"
            )
        }
        return null
    }
}
