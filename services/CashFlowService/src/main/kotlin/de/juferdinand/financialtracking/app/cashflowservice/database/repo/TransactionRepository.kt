package de.juferdinand.financialtracking.app.cashflowservice.database.repo

import de.juferdinand.financialtracking.app.cashflowservice.database.entity.Transaction
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface TransactionRepository : R2dbcRepository<Transaction, String> {
}