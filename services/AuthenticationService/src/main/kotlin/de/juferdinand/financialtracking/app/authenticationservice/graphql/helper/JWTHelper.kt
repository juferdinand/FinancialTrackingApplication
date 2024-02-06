package de.juferdinand.financialtracking.app.authenticationservice.graphql.helper

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.juferdinand.financialtracking.app.authenticationservice.database.entity.User
import de.juferdinand.financialtracking.app.authenticationservice.graphql.config.SpringJWTConfiguration
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class JWTHelper(private val springJWTConfiguration: SpringJWTConfiguration) {

    private val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm")

    fun createAccessJWT(user: User, maxAge: Long): String {
        val algorithm = Algorithm.HMAC512(springJWTConfiguration.secrets["access"])
        return JWT.create()
            .withSubject(user.userId)
            .withIssuer("Mynance")
            .withPayload(createPayload(user))
            .withExpiresAt(Date(System.currentTimeMillis() + maxAge))
            .sign(algorithm)
    }

    fun createRefreshJWT(user: User, maxAge: Long): String {
        val algorithm = Algorithm.HMAC512(springJWTConfiguration.secrets["refresh"])
        return JWT.create()
            .withSubject(user.userId)
            .withIssuer("Mynance")
            .withClaim("version", user.version)
            .withExpiresAt(Date(System.currentTimeMillis() + maxAge))
            .sign(algorithm)
    }

    private fun createPayload(user: User): Map<String, Any> {
        return mapOf(
            "firstname" to user.firstname,
            "surname" to user.surname,
            "email" to user.email,
            "created" to sdf.format(user.createdAt),
            "avatar" to user.avatarUrl
        )
    }

    companion object {
        fun getSubject(token: String): String {
            return JWT.decode(token).subject
        }
        fun getVersion(token: String): Long {
            return JWT.decode(token).getClaim("version").asLong()
        }
    }
}