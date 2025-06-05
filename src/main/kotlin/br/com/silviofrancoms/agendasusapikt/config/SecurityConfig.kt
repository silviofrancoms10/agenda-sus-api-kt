package br.com.silviofrancoms.agendasusapikt.config

import br.com.silviofrancoms.agendasusapikt.security.UserDetailsServiceImpl
import br.com.silviofrancoms.agendasusapikt.security.jwt.JwtAuthenticationFilter
import br.com.silviofrancoms.agendasusapikt.security.jwt.JwtTokenUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilita segurança em nível de método (@PreAuthorize)
class SecurityConfig(
    private val userDetailsService: UserDetailsServiceImpl,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return authProvider
    }

    @Bean
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .exceptionHandling {
                // ... (seu tratamento de exceção, se houver)
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/auth/**").permitAll() // Permite login
                    // A criação de usuário (POST /api/usuarios) pode ser para USUARIO ou ADMIN
                    // Se a criação for pública, mantenha .permitAll(). Caso contrário, proteja com hasRole.
                    // Exemplo para permitir apenas ADMIN criar: .requestMatchers(HttpMethod.POST, "/api/usuarios").hasRole("ADMIN")
                    // Por ora, vamos manter o cadastro aberto para novos usuários.
                    .requestMatchers("/api/usuarios").permitAll() // Apenas para o POST de cadastro
                    .requestMatchers("/api/usuarios/{id}").hasAnyRole("USUARIO", "ADMIN") // Usuário pode ver a si mesmo, admin pode ver qualquer um
                    .requestMatchers("/api/usuarios/**").hasRole("ADMIN") // GET (all), PUT, DELETE exigem ADMIN
                    .anyRequest().authenticated() // Qualquer outra requisição que não foi explicitamente permitida precisa ser autenticada
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenUtil, userDetailsService), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}