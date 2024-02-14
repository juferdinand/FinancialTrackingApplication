package de.juferdinand.financialtracking.app.cashflowservice.graphql.scalar

import com.netflix.graphql.dgs.DgsScalar
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import java.math.BigDecimal
import java.util.*

@DgsScalar(name = "BigDecimal")
class BigDecimalScalar : Coercing<BigDecimal, String> {
    override fun serialize(
        dataFetcherResult: Any,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): String? {
        if (dataFetcherResult is BigDecimal) {
            return dataFetcherResult.toString()
        }
        throw CoercingSerializeException("Expected a BigDecimal object.")
    }

    override fun parseValue(
        input: Any,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): BigDecimal? {
        try {
            return BigDecimal(input.toString())
        } catch (e: NumberFormatException) {
            throw CoercingParseValueException(
                "Failed to parse variable value $input to BigDecimal",
                e
            )
        }
    }

    override fun parseLiteral(
        input: Value<*>,
        variables: CoercedVariables,
        graphQLContext: GraphQLContext,
        locale: Locale
    ): BigDecimal? {
        if (input is StringValue) {
            try {
                return BigDecimal(input.value)
            } catch (e: NumberFormatException) {
                throw CoercingParseValueException("Failed to parse literal value to BigDecimal", e)
            }
        }
        throw CoercingParseValueException("Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'.")
    }
}
