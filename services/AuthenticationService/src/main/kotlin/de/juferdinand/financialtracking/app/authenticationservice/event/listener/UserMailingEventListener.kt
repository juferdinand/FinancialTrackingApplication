package de.juferdinand.financialtracking.app.authenticationservice.event.listener

import de.juferdinand.financialtracking.app.authenticationservice.event.UserMailingEvent
import de.juferdinand.financialtracking.app.authenticationservice.event.handler.MailHandler
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class UserMailingEventListener(private val mailHandler: MailHandler) {

    @EventListener
    fun sendMailEvent(event: UserMailingEvent) {
        event.user.flatMap { user ->
            Mono.fromCallable {
                mailHandler.sendMail(user.email, user.token, user.tokenType)
            }
        }.subscribe()
    }
}