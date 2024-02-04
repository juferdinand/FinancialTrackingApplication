package de.juferdinand.financialtracking.app.userservice

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.juferdinand.financialtracking.app.userservice.database.entity.User
import de.juferdinand.financialtracking.app.userservice.database.repo.UserRepository
import de.juferdinand.financialtracking.app.userservice.graphql.controller.GraphQLController
import de.juferdinand.financialtracking.app.userservice.graphql.response.RequestResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Profile
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.*

@SpringBootTest
@ExtendWith(SpringExtension::class)
@Testcontainers
@Profile("test")
class GraphQLControllerTest {

    @Autowired
    private lateinit var graphQLController: GraphQLController

    @Autowired
    private lateinit var userRepository: UserRepository

    private val jwt = JWT.create().withSubject("user_id")
        .withExpiresAt(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time)
        .sign(Algorithm.none())
    private val invalidJWT = JWT.create().withSubject("user_id")
        .withExpiresAt(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time)
        .sign(Algorithm.none())
    private val invalidUserJWT = JWT.create().withSubject("user_id2")
        .withExpiresAt(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time)
        .sign(Algorithm.none())

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
    fun changeUserInformationSuccessfully() {
        val userId = "user_id"

        val firstname = "John"
        val surname = "Doe"
        val email = "john.doe@example.com"

        // Erwartete Antwort
        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User information changed"
        )

        val response =
            graphQLController.changeUserInformation(firstname, surname, email, jwt).block()!!


        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
        val changedUser = userRepository.findUserByUserId(userId).block()!!
        assertEquals(changedUser.firstname, firstname)
        assertEquals(changedUser.surname, surname)
        assertEquals(changedUser.email, email)
    }

    @Test
    fun changeUserInformationButUserNotFound() {
        val firstname = "John"
        val surname = "Doe"
        val email = "john.doe@example"

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "1",
            message = "User not found"
        )

        val response =
            graphQLController.changeUserInformation(firstname, surname, email, invalidUserJWT)
                .block()!!


        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun changeUserInformationButTokenExpired() {
        val firstname = "John"
        val surname = "Doe"
        val email = "john.doe@example"

        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "4",
            message = "Token expired"
        )

        val response =
            graphQLController.changeUserInformation(firstname, surname, email, invalidJWT).block()!!

        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun deleteUserButTokenExpired() {
        val expectedResponse = RequestResponse(
            success = false,
            statusCode = "4",
            message = "Token expired"
        )

        val response = graphQLController.deleteUser(invalidJWT).block()!!

        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }

    @Test
    fun deleteUserSuccessfully() {
        val expectedResponse = RequestResponse(
            success = true,
            statusCode = "0",
            message = "User deleted"
        )

        val response = graphQLController.deleteUser(jwt).block()!!

        assertEquals(expectedResponse.success, response.success)
        assertEquals(expectedResponse.statusCode, response.statusCode)
        assertEquals(expectedResponse.message, response.message)
    }
}