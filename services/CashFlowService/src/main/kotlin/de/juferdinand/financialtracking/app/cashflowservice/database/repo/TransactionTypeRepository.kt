package de.juferdinand.financialtracking.app.cashflowservice.database.repo

import de.juferdinand.financialtracking.app.cashflowservice.database.entity.TransactionType
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface TransactionTypeRepository : R2dbcRepository<TransactionType, Int> {
}