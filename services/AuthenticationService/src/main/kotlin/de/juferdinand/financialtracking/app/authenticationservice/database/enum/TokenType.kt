package de.juferdinand.financialtracking.app.authenticationservice.database.enum

enum class TokenType(val subject: String) {




    EMAIL_VERIFICATION("Bitte bestätigen Sie Ihre E-Mail-Adresse für Mynance"),
    PASSWORD_RESET("Setzen Sie Ihr Mynance-Passwort jetzt zurück");
}