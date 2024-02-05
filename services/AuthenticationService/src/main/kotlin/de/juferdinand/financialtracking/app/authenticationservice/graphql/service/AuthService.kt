package de.juferdinand.financialtracking.app.authenticationservice.graphql.service

import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.authenticationservice.event.UserMailingEvent
import de.juferdinand.financialtracking.app.authenticationservice.graphql.exception.TokenCreationException
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.util.TokenGenerator
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val argon2: Argon2 = Argon2Factory.create()

    fun registerUser(
        email: String,
        password: String,
        firstname: String,
        surname: String,
        birth: String
    ): Mono<RequestResponse> {
        return userRepository.findByEmail(email)
            .flatMap {
                Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "6",
                        message = "User with email $email already exists"
                    )
                )
            }.switchIfEmpty(
                tokenGenerator.generateUniqueToken(8).flatMap { token ->
                    Mono.fromCallable { argon2.hash(2, 65536, 1, password.toByteArray()) }
                        .flatMap { hashedPassword ->
                            val user = User(
                                firstname = firstname,
                                surname = surname,
                                email = email,
                                password = hashedPassword,
                                token = token,
                                tokenValidUntil = LocalDate.now().plusDays(1)
                            )
                            userRepository.save(user).doOnSuccess {
                                applicationEventPublisher.publishEvent(
                                    UserMailingEvent(
                                        this,
                                        Mono.just(user)
                                    )
                                )
                            }
                                .map {
                                    RequestResponse(
                                        success = true,
                                        statusCode = "0",
                                        message = "User with email $email successfully registered, please verify your email address."
                                    )
                                }
                        }
                }
            ).onErrorResume { e ->
                if (e is TokenCreationException) {
                    Mono.just(
                        RequestResponse(
                            success = false,
                            statusCode = "8",
                            message = "An error occurred while generating token for user with email $email"
                        )
                    )
                } else {
                    Mono.just(
                        RequestResponse(
                            success = false,
                            statusCode = "7",
                            message = "An unexpected error occurred while registering user with email $email"
                        )
                    )
                }
            }
    }
}