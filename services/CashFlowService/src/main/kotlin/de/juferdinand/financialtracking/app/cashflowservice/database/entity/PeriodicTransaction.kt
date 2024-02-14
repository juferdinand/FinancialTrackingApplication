package de.juferdinand.financialtracking.app.cashflowservice.database.entity

import de.juferdinand.financialtracking.app.cashflowservice.database.enum.Period
import de.juferdinand.financialtracking.app.cashflowservice.database.enum.TransactionDirection
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.*

@Table("periodic_transaction", schema = "tables")
data class PeriodicTransaction(

    @Id
    @Column("periodic_transaction_id")
    val periodicTransactionId: String = UUID.randomUUID().toString(),

    @Column("transaction_type")
    val transactionTypeId: String,

    @Column("period")
    val period: Period,

    @Column("transaction_direction")
    val transactionDirection: TransactionDirection,

    @Column("amount")
    val amount: BigDecimal,

    @Column("description")
    val description: String,

    @Column("start_date")
    val startDate: Date? = null,

    @Column("end_date")
    val endDate: Date? = null,

    @Column("created_at")
    val createdAt: Date? = null,

    @Column("active")
    val active: Boolean = true
) : Persistable<String> {
    override fun getId(): String = periodicTransactionId
    override fun isNew(): Boolean = createdAt == null
}