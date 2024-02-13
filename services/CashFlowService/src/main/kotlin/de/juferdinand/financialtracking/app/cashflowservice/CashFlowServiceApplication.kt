package de.juferdinand.financialtracking.app.cashflowservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CashFlowServiceApplication

fun main(args: Array<String>) {
	runApplication<CashFlowServiceApplication>(*args)
}
