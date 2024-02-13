package de.juferdinand.financialtracking.app.cashflowservice.database.enum

enum class Period(private val description: String) {
    DAILY("Täglich"),
    WEEKLY("Wöchentlich"),
    MONTHLY("Monatlich"),
    YEARLY("Jährlich")
}