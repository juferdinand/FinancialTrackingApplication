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
        firstname: String,
        surname: String,
        email: String,
        access: String
    ): Mono<RequestResponse> {
        checkToken(access)?.let {
            return Mono.just(it)
        }
        return userRepository.findUserByUserId(jwtUtils.getSubjectFromToken(access)).flatMap {
            it.email = email
            it.firstname = firstname
            it.surname = surname
            userRepository.save(it).map {
                RequestResponse(
                    success = true,
                    statusCode = "0",
                    message = "User information changed"
                )
            }.onErrorResume {
                Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "2",
                        message = "Error while changing user information"
                    )
                )
            }
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "1",
                    message = "User not found"
                )
            )
        ).onErrorResume {
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "2",
                    message = "Error while changing user information"
                )
            )
        }
    }

    fun deleteUser(access: String): Mono<RequestResponse> {
        checkToken(access)?.let {
            return Mono.just(it)
        }
        return userRepository.deleteById(jwtUtils.getSubjectFromToken(access))
            .then(Mono.fromCallable {
                RequestResponse(
                    success = true,
                    statusCode = "0",
                    message = "User deleted"
                )
            }).onErrorResume {
                Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "2",
                        message = "Error while changing user information"
                    )
                )
            }
    }

    fun checkToken(access: String): RequestResponse? {
        if (!jwtUtils.isTokenValid(access)) {
            return RequestResponse(
                success = false,
                statusCode = "4",
                message = "Token expired"
            )
        }
        return null
    }
}
