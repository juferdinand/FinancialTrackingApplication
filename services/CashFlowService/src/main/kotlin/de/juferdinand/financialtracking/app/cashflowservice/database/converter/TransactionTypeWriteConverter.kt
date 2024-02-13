package de.juferdinand.financialtracking.app.cashflowservice.database.converter

import de.juferdinand.financialtracking.app.cashflowservice.database.enum.Period
import de.juferdinand.financialtracking.app.cashflowservice.database.enum.TransactionType
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class TransactionTypeWriteConverter : Converter<TransactionType, String> {
    override fun convert(tokenType: TransactionType): String {
        return tokenType.name
    }
}

@ReadingConverter
class TransactionTypeReadConverter : Converter<String, TransactionType> {
    override fun convert(tokenType: String): TransactionType {
        return TransactionType.valueOf(tokenType)
    }
}

@WritingConverter
class PeriodWriteConverter : Converter<Period, String> {
    override fun convert(periodType: Period): String {
        return periodType.name
    }
}

@ReadingConverter
class PeriodReadConverter : Converter<String, Period> {
    override fun convert(periodType: String): Period {
        return Period.valueOf(periodType)
    }
}

