package de.juferdinand.financialtracking.app.userservice.database.repo

import de.juferdinand.financialtracking.app.userservice.database.entity.User
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepository : R2dbcRepository<User, String> {

    fun findUserByUserId(userId: String): Mono<User>
}