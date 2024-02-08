package de.juferdinand.financialtracking.app.authenticationservice.database.entity

import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Table(name = "users", schema = "tables")
data class User(
    @Id
    @Column("user_id")
    var userId: String = UUID.randomUUID().toString(),

    @Column("firstname")
    var firstname: String,

    @Column("surname")
    var surname: String,

    @Column("email")
    var email: String,

    @Column("password")
    var password: String?,

    @Column("token")
    var token: String?,

    @Column("token_valid_until")
    var tokenValidUntil: LocalDateTime?,

    @Column("token_type")
    var tokenType: TokenType? = TokenType.EMAIL_VERIFICATION,

    @Column("verified")
    var verified: Boolean = false,

    @Column("created_at")
    var createdAt: LocalDateTime? = null,

    @Column("avatar_url")
    var avatarUrl: String = "default_avatar.png", //TODO: change to default avatar

    @Column("version")
    var version : Long = 0

) : Persistable<String> {

    override fun getId(): String? = userId
    override fun isNew(): Boolean = createdAt == null
}