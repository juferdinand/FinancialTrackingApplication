package de.juferdinand.financialtracking.app.cashflowservice.database.repo

import de.juferdinand.financialtracking.app.cashflowservice.database.entity.PeriodicTransaction
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface PeriodicTransactionRepository:R2dbcRepository<PeriodicTransaction, String> {
}