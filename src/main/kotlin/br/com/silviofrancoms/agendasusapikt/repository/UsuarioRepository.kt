package br.com.silviofrancoms.agendasusapikt.repository

import br.com.silviofrancoms.agendasusapikt.model.Usuario
import org.springframework.data.jpa.repository.JpaRepository

interface UsuarioRepository : JpaRepository<Usuario, Long> {

    fun findByEmail(email: String): Usuario?
}