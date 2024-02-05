package de.juferdinand.financialtracking.app.authenticationservice.event

import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import org.springframework.context.ApplicationEvent
import reactor.core.publisher.Mono

class UserMailingEvent(source: Any, val user: Mono<User>) : ApplicationEvent(source)