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
// Importação necessária para AntPathRequestMatcher (se você precisar ignorar o CSRF se ele estiver habilitado)
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

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
            .csrf { csrf -> // Use 'csrf' para configurar o CSRF
                // Como você já tem 'it.disable()', o CSRF está totalmente desativado para a API.
                // No entanto, para o H2 Console, é crucial que ele não seja afetado.
                // Se 'it.disable()' não resolver o problema de CSRF para o H2 Console,
                // você pode tentar 'csrf.ignoringRequestMatchers(AntPathRequestMatcher("/h2-console/**"))'
                // ANTES de 'csrf.disable()', embora geralmente 'disable()' já cubra isso.
                csrf.disable() // Desabilita CSRF completamente para sua API REST
            }
            .headers { headers -> // Configura cabeçalhos de segurança
                headers.frameOptions { frameOptions -> // Permite que o H2 Console seja carregado em um iframe
                    frameOptions.sameOrigin() // Permite iframes do mesmo domínio (necessário para H2)
                }
            }
            .exceptionHandling {
                // ... (seu tratamento de exceção, se houver)
            }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // JWT é sem estado
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/auth/**").permitAll() // Permite login
                    // A criação de usuário (POST /api/usuarios) é permitida publicamente
                    .requestMatchers("/api/usuarios").permitAll()

                    // --- ADIÇÕES PARA O H2 CONSOLE ---
                    // Permite acesso irrestrito ao endpoint do H2 Console
                    .requestMatchers("/h2/**").permitAll() // Certifique-se que este é o caminho correto
                    // Se o console estiver em '/h2' em vez de '/h2-console', adicione também:
                    // .requestMatchers("/h2/**").permitAll()
                    // --- FIM DAS ADIÇÕES PARA O H2 CONSOLE ---

                    .requestMatchers("/api/usuarios/{id}").hasAnyRole("USUARIO", "ADMIN") // Usuário pode ver a si mesmo, admin pode ver qualquer um
                    .requestMatchers("/api/usuarios/**").hasAnyRole("USUARIO", "ADMIN") // GET (all), PUT, DELETE exigem ADMIN
                    .anyRequest().authenticated() // Qualquer outra requisição que não foi explicitamente permitida precisa ser autenticada
            }
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(JwtAuthenticationFilter(jwtTokenUtil, userDetailsService), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}