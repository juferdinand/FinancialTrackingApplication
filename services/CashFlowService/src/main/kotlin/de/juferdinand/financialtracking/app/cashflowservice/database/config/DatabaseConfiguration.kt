package de.juferdinand.financialtracking.app.cashflowservice.database.config

import de.juferdinand.financialtracking.app.cashflowservice.database.converter.PeriodReadConverter
import de.juferdinand.financialtracking.app.cashflowservice.database.converter.PeriodWriteConverter
import de.juferdinand.financialtracking.app.cashflowservice.database.converter.TransactionTypeReadConverter
import de.juferdinand.financialtracking.app.cashflowservice.database.converter.TransactionTypeWriteConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import io.r2dbc.spi.ConnectionFactory

@Configuration
@EnableR2dbcRepositories
class DatabaseConfiguration(
    private val connectionFactory: ConnectionFactory
) : AbstractR2dbcConfiguration() {

    override fun connectionFactory(): ConnectionFactory {
        return connectionFactory
    }

    override fun getCustomConverters(): MutableList<Any> {
        return mutableListOf(
            TransactionTypeReadConverter(),
            TransactionTypeWriteConverter(),
            PeriodReadConverter(),
            PeriodWriteConverter()
        )
    }
}