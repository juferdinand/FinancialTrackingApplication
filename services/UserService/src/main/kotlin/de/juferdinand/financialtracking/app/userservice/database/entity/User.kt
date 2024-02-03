package de.juferdinand.financialtracking.app.userservice.database.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("users")
data class User(

    @Id
    @Column("user_id")
    val userId: String = UUID.randomUUID().toString(),

    @Column("firstname")
    var firstname: String,

    @Column("surname")
    var surname: String,

    @Column("email")
    var email: String
)