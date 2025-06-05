package br.com.silviofrancoms.agendasusapikt.security.jwt

import io.jsonwebtoken.Claims // Adicione esta
import io.jsonwebtoken.ExpiredJwtException // Adicione esta
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException // Adicione esta
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException // Adicione esta
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException // Adicione esta
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import java.util.function.Function // Adicione esta, se for usar getClaimFromToken

@Component
class JwtTokenUtil {

    @Value("\${jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration}")
    private var jwtExpirationMs: Long = 86400000 // 24 horas

    private val logger = java.util.logging.Logger.getLogger(JwtTokenUtil::class.java.name) // Adicione esta linha de logger

    private fun getSigningKey(): SecretKey {
        val keyBytes = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        claims["roles"] = userDetails.authorities.map { it.authority } // Adicionei a parte de roles aqui

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
            return true
        } catch (ex: SignatureException) {
            logger.warning("Assinatura JWT inválida: ${ex.message}")
        } catch (ex: MalformedJwtException) {
            logger.warning("Token JWT inválido: ${ex.message}")
        } catch (ex: ExpiredJwtException) {
            logger.warning("Token JWT expirado: ${ex.message}")
        } catch (ex: UnsupportedJwtException) {
            logger.warning("Token JWT não suportado: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            logger.warning("Cadeia de claims JWT vazia: ${ex.message}")
        }
        return false
    }

    // Métodos para extrair informações do token, como sugerido na última resposta
    fun getUsernameFromToken(token: String): String {
        return getClaimFromToken(token, Claims::getSubject)
    }

    fun getExpirationDateFromToken(token: String): Date {
        return getClaimFromToken(token, Claims::getExpiration)
    }

    fun <T> getClaimFromToken(token: String, claimsResolver: Function<Claims, T>): T {
        val claims = getAllClaimsFromToken(token)
        return claimsResolver.apply(claims)
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .body
    }
}