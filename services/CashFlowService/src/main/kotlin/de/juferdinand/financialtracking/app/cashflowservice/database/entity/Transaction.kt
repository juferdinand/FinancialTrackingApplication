package de.juferdinand.financialtracking.app.cashflowservice.database.entity

import de.juferdinand.financialtracking.app.cashflowservice.database.enum.TransactionDirection
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.*

@Table(name = "transaction", schema = "tables")
data class Transaction(
    @Id
    @Column(value = "transaction_id")
    val transactionId: String= UUID.randomUUID().toString(),

    @Column(value = "amount")
    val amount: BigDecimal,

    @Column(value = "transaction_type")
    val transactionDirection: TransactionDirection,

    @Column(value = "transaction_date")
    val transactionDate: Date? = null,

    @Column(value = "description")
    val description: String,

    @Column(value = "pericodic_transaction_id")
    val periodicTransactionId: String? = null

) : Persistable<String> {
    override fun getId(): String = transactionId
    override fun isNew(): Boolean = transactionDate == null
}