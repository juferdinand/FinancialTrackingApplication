package de.juferdinand.financialtracking.app.userservice.database.entity

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
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

    @Column("created_at")
    var createdAt: LocalDate? = null

) : Persistable<String> {

    override fun getId(): String? = userId
    override fun isNew(): Boolean = createdAt == null
}