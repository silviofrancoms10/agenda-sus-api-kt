package br.com.silviofrancoms.agendasusapikt.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ExampleException(exeption: String): RuntimeException(exeption) {
}