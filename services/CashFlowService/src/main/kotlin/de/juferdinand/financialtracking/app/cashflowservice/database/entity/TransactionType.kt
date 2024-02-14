package de.juferdinand.financialtracking.app.cashflowservice.database.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "transaction_type", schema = "tables")
data class TransactionType(
    @Id
    @Column(value = "transaction_type_id")
    val transactionTypeId: Int,

    @Column(value = "name")
    val name: String,

    @Column(value = "icon")
    val icon: String,
)