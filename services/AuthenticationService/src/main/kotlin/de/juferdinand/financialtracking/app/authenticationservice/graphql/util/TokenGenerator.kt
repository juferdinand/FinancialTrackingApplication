package de.juferdinand.financialtracking.app.authenticationservice.graphql.util

import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.authenticationservice.graphql.exception.TokenCreationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.util.retry.Retry

@Component
class TokenGenerator(
    private val userRepository: UserRepository
) {

    @Throws(TokenCreationException::class)
    fun generateUniqueToken(length: Int): Mono<String> {
        return Mono.defer {
            val token = generateToken(length)
            userRepository.findByToken(token)
                .flatMap { Mono.error<String>(TokenCreationException()) }
                .switchIfEmpty(Mono.just(token))
        }
            .retryWhen(Retry.max(10))
    }

    private fun generateToken(length: Int): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}