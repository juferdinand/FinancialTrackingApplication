package de.juferdinand.financialtracking.app.authenticationservice

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.juferdinand.financialtracking.app.authenticationservice.entity.GraphQLResponse
import de.juferdinand.financialtracking.app.authenticationservice.graphql.controller.GraphQLController
import de.juferdinand.financialtracking.app.authenticationservice.graphql.response.RequestResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@Testcontainers
@AutoConfigureWebTestClient
@Profile("test")
class GraphQLControllerTest {

    @Autowired
    private lateinit var graphQLController: GraphQLController

    @Autowired
    private lateinit var webTestClient: WebTestClient

    companion object {
        @Container
        @ServiceConnection
        val database = PostgreSQLContainer<Nothing>("postgres:16").apply {
            withDatabaseName("financedb")
            withUsername("postgres")
            withPassword("test")
        }
    }

    @Test
    fun registerUserSuccessfully() {
        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User successfully registered"
        )

        val requestResponse =
            graphQLController.registerUser("John.Doe@example.de", "example_test", "John", "Doe")
                .block()!!

        assertEquals(expectedResponse.success, requestResponse.success)
        assertEquals(expectedResponse.statusCode, requestResponse.statusCode)
        assertEquals(expectedResponse.message, requestResponse.message)
    }

    @Test
    fun registerUserButEmailAlreadyExists() {
        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "6",
            message = "User with this email already exists"
        )

        val requestResponse =
            graphQLController.registerUser("Max.Mustermann@example.de", "test", "Max", "Mustermann")
                .block()!!

        assertEquals(expectedResponse.success, requestResponse.success)
        assertEquals(expectedResponse.statusCode, requestResponse.statusCode)
        assertEquals(expectedResponse.message, requestResponse.message)
    }

    @Test
    fun loginUserButUserIsNotVerified() {

        val query = """
               query{
                   loginUser(email:"Max.Mustermann@example.de", password:"test"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "12",
            message = "User is not verified"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .expectCookie().doesNotExist("access")
            .expectCookie().doesNotExist("refresh")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["loginUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun loginUserSuccessfully() {

        val query = """
               query{
                   loginUser(email:"Theo.Mustermann@example.de", password:"test"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User successfully logged in"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("access")
            .expectCookie().exists("refresh")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["loginUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun loginUserButUserNotFound() {
        val query = """
               query{
                   loginUser(email:"Theo.Musterman@example.de", password:"test"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "5",
            message = "Email or password is incorrect"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .expectCookie().doesNotExist("access")
            .expectCookie().doesNotExist("refresh")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["loginUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun loginButPasswordIsIncorrect() {
        val query = """
               query{
                   loginUser(email:"Theo.Mustermann@example.de", password:"incorrectPassword"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse =
            RequestResponse(
                success = false,
                statusCode = "5",
                message = "Email or password is incorrect"
            )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .expectCookie().doesNotExist("access")
            .expectCookie().doesNotExist("refresh")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["loginUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun refreshTokenSuccessfully() {
        val query = """
               query{
                   refreshToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "Token successfully refreshed"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id_verified"))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("access")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["refreshToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun refreshTokenButUserNotFound() {
        val query = """
               query{
                   refreshToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "9",
            message = "User not found or refresh token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id_not_found"))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["refreshToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun refreshTokenButRefreshTokenIsInvalid() {
        val query = """
               query{
                   refreshToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "10",
            message = "Refresh token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id", 1))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["refreshToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun revokeTokenSuccessfully() {
        val query = """
               mutation{
                   revokeToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "Token successfully revoked"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id_verified"))
            .exchange()
            .expectStatus().isOk
            .expectCookie().doesNotExist("access")
            .expectCookie().value("refresh") {
                val decodedJWT = JWT.require(Algorithm.HMAC512("5678"))
                    .build().verify(it)
                assertEquals(1L, decodedJWT.getClaim("version").asLong())
            }
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["revokeToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun revokeTokenButUserIdNotValid() {
        val query = """
               mutation{
                   revokeToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "9",
            message = "User not found or refresh token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id_invalid"))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["revokeToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun revokeTokenButVersionNotValid() {
        val query = """
               mutation{
                   revokeToken{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "10",
            message = "Refresh token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("refresh", createRefreshToken("user_id_verified", 1L))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["revokeToken"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyUserSuccessfully() {
        val query = """
               mutation{
                   verifyUser(token:"token"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User successfully verified"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyUserButTokenDoesNotExists() {
        val query = """
               mutation{
                   verifyUser(token:"invalid_token"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "11",
            message = "Token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyUserButTokenTypeIsNotEmailVerification() {
        val query = """
               mutation{
                   verifyUser(token:"token3"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "16",
            message = "Token is not an email verification token"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyUserButTokenIsInvalid() {
        val query = """
               mutation{
                   verifyUser(token:"token2"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "11",
            message = "Token is invalid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun logoutUserSuccessfully() {
        val query = """
               query{
                   logoutUser{
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User successfully logged out"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .cookie("access", "access")
            .cookie("refresh", "refresh")
            .exchange()
            .expectStatus().isOk
            .expectCookie().maxAge("access", Duration.ZERO)
            .expectCookie().maxAge("refresh", Duration.ZERO)
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["logoutUser"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun requestPasswordResetButUserNotVerified() {
        val query = """
               query{
                   requestPasswordReset(email:"Tim.Mustermann@example.de"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
         """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "12",
            message = "User is not verified"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["requestPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun requestPasswordResetButUserAlreadyHasToken() {
        val query = """
               query{
                   requestPasswordReset(email:"Herbert.Mustermann@example.de"){
                       success
                       message
                       statusCode
                       timestamp
                   }}
                   """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "17",
            message = "User already has a token"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["requestPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun requestPasswordResetButUserNotFound() {
        val query = """
               query{
                   requestPasswordReset(email:"Martin.Mustermann@example.de"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
         """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "11",
            message = "User not found"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["requestPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun requestPasswordResetSuccessfully() {
        val query = """
               query{
                   requestPasswordReset(email:"Theo.Mustermann@example.de"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
         """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "Password reset token sent"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["requestPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyTokenForPasswordResetButTokenIsInvalid() {
        val query = """
               query{
                   verifyTokenForPasswordReset(token:"invalid_token"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
         """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "13",
            message = "Token is invalid"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyTokenForPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyTokenForPasswordResetButTokenTypeIsNotPasswordReset() {
        val query = """
               query{
                   verifyTokenForPasswordReset(token:"token4"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "15",
            message = "Token is not a password reset token"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyTokenForPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyTokenForPasswordResetButTokenIsExpired() {
        val query = """
               query{
                   verifyTokenForPasswordReset(token:"token2"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "14",
            message = "Token is expired"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyTokenForPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun verifyTokenForPasswordResetSuccessfully() {
        val query = """
               query{
                   verifyTokenForPasswordReset(token:"token3"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "Token is valid"
        )
        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["verifyTokenForPasswordReset"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun resetPasswordButTokenIsInvalid() {
        val query = """
               mutation{
                   resetPassword(token:"invalid_token", password:"new_password"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "13",
            message = "Token is invalid"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["resetPassword"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun resetPasswordButTokenTypeIsNotPasswordReset() {
        val query = """
               mutation{
                   resetPassword(token:"token4", password:"new_password"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "15",
            message = "Token is not a password reset token"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["resetPassword"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun resetPasswordButTokenIsExpired() {
        val query = """
               mutation{
                   resetPassword(token:"token2", password:"new_password"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "14",
            message = "Token is expired"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["resetPassword"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun resetPasswordSuccessfully() {
        val query = """
               mutation{
                   resetPassword(token:"token5", password:"new_password"){
                       success
                       message
                       statusCode
                       timestamp
                   }
               }
        """.trimIndent()

        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "Password reset successful"
        )

        val graphQLResponse = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to query))
            .exchange()
            .expectStatus().isOk
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val response = graphQLResponse.data["resetPassword"]!!
        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)

        //login check
        val queryLogin = """
               query{
                   loginUser(email:"Nils.Mustermann@example.de", password:"new_password"){
                       success
                       message
                       statusCode
                       timestamp
                   }
                   }
                   """.trimIndent()

        val expectedResponseLogin = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User successfully logged in"
        )

        val graphQLResponseLogin = webTestClient.post()
            .uri("/graphql")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(mapOf("query" to queryLogin))
            .exchange()
            .expectStatus().isOk
            .expectCookie().exists("access")
            .expectCookie().exists("refresh")
            .returnResult(GraphQLResponse::class.java)
            .responseBody.blockFirst()!!

        val responseLogin = graphQLResponseLogin.data["loginUser"]!!
        assertEquals(expectedResponseLogin.success, responseLogin.success)
        assertEquals(expectedResponseLogin.statusCode, responseLogin.statusCode)
        assertEquals(expectedResponseLogin.message, responseLogin.message)
    }

    private fun createRefreshToken(subject: String, version: Long = 0L): String {
        return JWT.create()
            .withSubject(subject)
            .withIssuer("Mynance")
            .withPayload(mapOf("version" to version))
            .sign(Algorithm.HMAC512("5678"))
    }
}