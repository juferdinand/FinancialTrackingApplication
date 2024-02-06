package de.juferdinand.financialtracking.app.authenticationservice.graphql.helper

import org.springframework.http.ResponseCookie

class CookieHelper {
    companion object {
        fun createCookie(
            name: String,
            value: String,
            maxAge: Long,
            secure: Boolean= false,
            httpOnly: Boolean
        ): ResponseCookie {
            return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .secure(secure)
                .httpOnly(httpOnly)
                .build()
        }
    }
}