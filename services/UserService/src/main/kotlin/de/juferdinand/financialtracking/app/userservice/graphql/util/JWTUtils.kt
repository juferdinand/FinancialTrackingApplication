package de.juferdinand.financialtracking.app.userservice.graphql.util

import com.auth0.jwt.JWT
import org.springframework.stereotype.Component
import java.util.*

@Component
class JWTUtils {

    fun isTokenValid(token: String): Boolean {
        return !JWT.decode(token).expiresAt.before(Date())
    }

    fun getSubjectFromToken(access: String): String {
        return JWT.decode(access).subject
    }
}