package de.juferdinand.financialtracking.app.authenticationservice.database.converter

import de.juferdinand.financialtracking.app.authenticationservice.database.enum.TokenType
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class TokenTypeWriteConverter : Converter<TokenType, String> {
    override fun convert(tokenType: TokenType): String {
        return tokenType.name
    }
}

@ReadingConverter
class TokenTypeReadConverter : Converter<String, TokenType> {
    override fun convert(tokenType: String): TokenType {
        return TokenType.valueOf(tokenType)
    }
}