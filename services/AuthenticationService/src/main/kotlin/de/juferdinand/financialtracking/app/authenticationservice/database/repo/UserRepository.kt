package de.juferdinand.financialtracking.app.authenticationservice.database.repo

import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

interface UserRepository : R2dbcRepository<User, String> {

    fun findByToken(token: String): Mono<User>
    fun findByEmail(email: String): Mono<User>

    fun findAllByVerifiedIsFalseAndTokenValidUntilBefore(localDateTime: LocalDateTime): Mono<User>
}