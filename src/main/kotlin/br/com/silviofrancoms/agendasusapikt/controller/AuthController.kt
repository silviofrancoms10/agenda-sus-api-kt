// AuthController.kt (sem alterações necessárias, mas mostrando para contexto)
package br.com.silviofrancoms.agendasusapikt.controller

import br.com.silviofrancoms.agendasusapikt.security.jwt.JwtTokenUtil
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

data class AuthenticationRequest(
    val email: String,
    val senha: String
)

data class AuthenticationResponse(
    val token: String
)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtTokenUtil: JwtTokenUtil
) {
    private val logger = Logger.getLogger(AuthController::class.java.name)

    @PostMapping("/login")
    fun createAuthenticationToken(@RequestBody authenticationRequest: AuthenticationRequest): ResponseEntity<AuthenticationResponse> {
        logger.info("Requisição de login para o email: ${authenticationRequest.email}")
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(authenticationRequest.email, authenticationRequest.senha)
            )
        } catch (e: Exception) {
            logger.warning("Falha na autenticação para o email: ${authenticationRequest.email} - ${e.message}")
            return ResponseEntity.badRequest().build()
        }

        val userDetails: UserDetails = userDetailsService.loadUserByUsername(authenticationRequest.email)
        val jwt: String = jwtTokenUtil.generateToken(userDetails)

        logger.info("Login bem-sucedido para o email: ${authenticationRequest.email}")
        return ResponseEntity.ok(AuthenticationResponse(jwt))
    }
}