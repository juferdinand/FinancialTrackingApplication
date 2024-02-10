package de.juferdinand.financialtracking.app.authenticationservice.task

import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import de.juferdinand.financialtracking.app.authenticationservice.database.repo.UserRepository
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@EnableScheduling
class DeleteInvalidUser(private val userRepository: UserRepository) {

    @Scheduled(cron = "0 0 0 * * ?")
    fun deleteInvalidUser() {
        userRepository.findAllByVerifiedIsFalseAndTokenValidUntilBefore(LocalDateTime.now())
            .map { user ->
                if (user.tokenType == TokenType.EMAIL_VERIFICATION) {
                    userRepository.delete(user)
                }else{
                    user.token = null
                    user.tokenType = null
                    user.tokenValidUntil = null
                    userRepository.save(user)
                }
            }.subscribe()
    }
}