package de.juferdinand.financialtracking.app.authenticationservice.graphql.helper

import io.netty.handler.codec.http.cookie.Cookie
import io.netty.handler.codec.http.cookie.CookieHeaderNames
import org.springframework.boot.web.server.Cookie.SameSite
import org.springframework.http.ResponseCookie

class CookieHelper {
    companion object {
        fun createCookie(
            name: String,
            value: String,
            maxAge: Long,
            secure: Boolean= true,
            httpOnly: Boolean
        ): ResponseCookie {
            return ResponseCookie.from(name, value)
                .maxAge(maxAge)
                .secure(secure)
                .sameSite(CookieHeaderNames.SameSite.None.name)
                .httpOnly(httpOnly)
                .build()
        }
    }
}