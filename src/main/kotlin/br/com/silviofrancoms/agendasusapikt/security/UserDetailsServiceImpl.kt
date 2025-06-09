package br.com.silviofrancoms.agendasusapikt.security

import br.com.silviofrancoms.agendasusapikt.repository.UsuarioRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val usuarioRepository: UsuarioRepository
) : UserDetailsService {

    override fun loadUserByUsername(email: String?): UserDetails {
        if (email == null) {
            throw UsernameNotFoundException("Email não pode ser nulo.")
        }
        val usuario = usuarioRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("Usuário não encontrado com email: $email")

        // Converte a string de roles (ex: "ADMIN,USUARIO") em uma lista de GrantedAuthority
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        usuario.roles.split(",").forEach { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_${role.trim().uppercase()}"))
        }
        // As roles no Spring Security DEVEM começar com "ROLE_" por convenção para @PreAuthorize etc.
        // Ex: "ROLE_ADMIN", "ROLE_USUARIO"

        return User(
            usuario.email,          // username (email)
            usuario.senha,          // senha hashed
            authorities             // authorities (roles)
        )
    }
}