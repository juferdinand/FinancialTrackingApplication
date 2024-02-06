package de.juferdinand.financialtracking.app.authenticationservice.graphql.service

import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.authenticationservice.event.UserMailingEvent
import de.juferdinand.financialtracking.app.authenticationservice.graphql.exception.TokenCreationException
import de.juferdinand.financialtracking.app.authenticationservice.graphql.helper.CookieHelper
import de.juferdinand.financialtracking.app.authenticationservice.graphql.helper.JWTHelper
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.util.TokenGenerator
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val tokenGenerator: TokenGenerator,
    private val jwtHelper: JWTHelper,
    private val applicationEventPublisher: ApplicationEventPublisher
) {
    private val argon2: Argon2 = Argon2Factory.create()

    private val accessJWTMaxAge = 60 * 15.toLong()
    private val refreshJWTMaxAge = 60 * 60 * 24 * 30.toLong()

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
                            }.flatMap {
                                createSuccessResponse("User successfully registered")
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
                            message = "An unexpected error occurred while registering user"
                        )
                    )
                }
            }
    }

    fun loginUser(
        email: String,
        password: String,
        serverHttpResponse: ServerHttpResponse
    ): Mono<RequestResponse> {
        return userRepository.findByEmail(email).flatMap { user ->
            Mono.just(argon2.verify(user.password, password.toByteArray())).flatMap { verified ->
                if (verified) {
                    val refreshMono = Mono.fromRunnable<Unit> {
                        serverHttpResponse.cookies.add(
                            "refresh",
                            CookieHelper.createCookie(
                                "refresh",
                                jwtHelper.createRefreshJWT(user, refreshJWTMaxAge),
                                refreshJWTMaxAge,
                                httpOnly = true
                            )
                        )
                    }
                    Mono.`when`(createAccessCookie(user, serverHttpResponse), refreshMono).then(
                        createSuccessResponse("User successfully logged in")
                    )
                } else {
                    Mono.just(
                        RequestResponse(
                            success = false,
                            statusCode = "5",
                            message = "Email or password is incorrect"
                        )
                    )
                }
            }
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "9",
                    message = "User with email $email does not exist"
                )
            )
        ).onErrorResume {
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while logging in user"
                )
            )
        }
    }

    fun refreshToken(
        serverHttpResponse: ServerHttpResponse,
        refresh: String
    ): Mono<RequestResponse> {
        return userRepository.findById(JWTHelper.getSubject(refresh)).flatMap { user ->
            if (JWTHelper.getVersion(refresh) == user.version) {
                return@flatMap Mono.just(
                    RequestResponse(
                        success = false,
                        statusCode = "10",
                        message = "Refresh token is invalid"
                    )
                )
            }
            Mono.`when`(createAccessCookie(user, serverHttpResponse)).then(
                createSuccessResponse("Token successfully refreshed")
            )
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while refreshing token"
                )
            )
        ).onErrorResume {
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while refreshing token"
                )
            )
        }
    }

    fun revokeToken(
        serverHttpResponse: ServerHttpResponse,
        refresh: String
    ): Mono<RequestResponse> {
        return userRepository.findById(JWTHelper.getSubject(refresh)).flatMap { user ->
            user.version++
            userRepository.save(user).then(
                Mono.fromRunnable<Unit> {
                    serverHttpResponse.cookies.add(
                        "refresh",
                        CookieHelper.createCookie(
                            "refresh",
                            "",
                            refreshJWTMaxAge,
                            httpOnly = true
                        )
                    )
                }.then(
                    createSuccessResponse("Token successfully revoked")
                )
            )
        }.switchIfEmpty(
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while revoking token"
                )
            )
        ).onErrorResume {
            Mono.just(
                RequestResponse(
                    success = false,
                    statusCode = "7",
                    message = "An unexpected error occurred while revoking token"
                )
            )
        }
    }

    private fun createAccessCookie(user: User, serverHttpResponse: ServerHttpResponse): Mono<Unit> {
        return Mono.fromRunnable<Unit> {
            serverHttpResponse.cookies.add(
                "access",
                CookieHelper.createCookie(
                    "access",
                    jwtHelper.createAccessJWT(user, accessJWTMaxAge),
                    accessJWTMaxAge,
                    httpOnly = false
                )
            )
        }
    }

    private fun createSuccessResponse(message: String): Mono<RequestResponse> {
        return Mono.just(
            RequestResponse(
                success = true,
                statusCode = "0",
                message = message
            )
        )
    }

}