package de.juferdinand.financialtracking.app.authenticationservice.event.listener

import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.authenticationservice.event.UserMailingEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserMailingEventListener(private val userRepository: UserRepository) {

    @EventListener
    fun sendMail(event: UserMailingEvent) {
        event.user.flatMap { user ->
            Mono.fromCallable {
                println("Sending mail to ${user.email}")
            }
        }.subscribe()
    }
}