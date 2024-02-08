package de.juferdinand.financialtracking.app.authenticationservice.graphql.service

import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.authenticationservice.event.UserMailingEvent
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.util.TokenGenerator
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val applicationEventPublisher: ApplicationEventPublisher
) {

    private val argon2: Argon2 = Argon2Factory.create()

    fun requestPasswordReset(email: String): Mono<RequestResponse> {
        return userRepository.findByEmail(email).flatMap { user ->
            if (!user.verified) {
                return@flatMap Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "12",
                        message = "User is not verified"
                    )
                )
            }else if(user.token != null && user.tokenValidUntil!!.isAfter(LocalDateTime.now())){
                return@flatMap Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "17",
                        message = "User already has a token"
                    )
                )
            }
            tokenGenerator.generateUniqueToken(8).flatMap { token ->
                user.token = token
                user.tokenValidUntil = LocalDateTime.now().plusDays(1)
                user.tokenType = TokenType.PASSWORD_RESET
                userRepository.save(user).doOnSuccess {
                    applicationEventPublisher.publishEvent(
                        UserMailingEvent(
                            this,
                            Mono.just(it)
                        )
                    )
                }.flatMap {
                    Mono.just(
                        RequestResponse(
                            success = true,
                            statusCode = "0",
                            message = "Password reset token sent"
                        )
                    )
                }
            }
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "11",
                    message = "User not found"
                )
            )
        ).onErrorResume {
            it.printStackTrace()
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while logging in user"
                )
            )
        }
    }

    fun verifyTokenForPasswordReset(token: String): Mono<RequestResponse> {
        return userRepository.findByToken(token).flatMap {user ->
            checkToken(user).switchIfEmpty(
                Mono.just(
                    RequestResponse(
                        success = true,
                        statusCode = "0",
                        message = "Token is valid"
                    )
                )
            )
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "13",
                    message = "Token is invalid"
                )
            )
        ).onErrorResume {
            it.printStackTrace()
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while verifying token"
                )
            )
        }
    }

    fun resetPassword(token: String, password: String): Mono<RequestResponse> {
        return userRepository.findByToken(token).flatMap { user ->
            checkToken(user).switchIfEmpty(
                Mono.defer {
                    user.password = argon2.hash(2, 65536, 1, password.toCharArray())
                    user.token = null
                    user.tokenValidUntil = null
                    user.tokenType = null
                    userRepository.save(user).map {
                        RequestResponse(
                            success = true,
                            statusCode = "0",
                            message = "Password reset successful"
                        )
                    }
                }
            )
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "13",
                    message = "Token is invalid"
                )
            )
        ).onErrorResume {
            it.printStackTrace()
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while resetting password"
                )
            )
        }
    }

    private fun checkToken(user: User): Mono<RequestResponse> {
        if (user.tokenValidUntil!!.isBefore(LocalDateTime.now())) {
            return Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "14",
                    message = "Token is expired"
                )
            )
        }
        if (user.tokenType != TokenType.PASSWORD_RESET) {
            return Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "15",
                    message = "Token is not a password reset token"
                )
            )
        }
        return Mono.empty()
    }
}