package de.juferdinand.financialtracking.app.authenticationservice.database.enum

enum class TokenType(val subject: String, val path:String) {




    EMAIL_VERIFICATION("Bitte bestätigen Sie Ihre E-Mail-Adresse für Mynance", "verify"),
    PASSWORD_RESET("Setzen Sie Ihr Mynance-Passwort jetzt zurück", "reset-password"),
}