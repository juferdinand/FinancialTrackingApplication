package de.juferdinand.financialtracking.app.authenticationservice.event.handler

import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class MailHandler(private val javaMailSender: JavaMailSender) {

    fun sendMail(email: String, token: String, tokenType: TokenType) {
        val mailTemplate = readMailTemplate(tokenType)
        val mailMessageHelper = MimeMessageHelper(javaMailSender.createMimeMessage(), true, "UTF-8")
        mailMessageHelper.setFrom("noreply@structuremade.de")
        mailMessageHelper.setTo(email)
        mailMessageHelper.setSubject(tokenType.subject)
        mailMessageHelper.setText(
            mailTemplate.replace(
                "%button-url%",
                "placeholder-will-be-replace-with-url/verify?token=$token"
            ) , true
        )
        javaMailSender.send(mailMessageHelper.mimeMessage)
    }

    private fun readMailTemplate(tokenType: TokenType): String {
        val inputStream =
            this::class.java.getResourceAsStream("/mail/${tokenType.name.lowercase()}.html")!!
        return BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).readText()
    }
}