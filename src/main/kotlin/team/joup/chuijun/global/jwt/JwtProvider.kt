package team.joup.chuijun.global.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtProvider(private val properties: JwtProperties) {

    private val key by lazy {
        Keys.hmacShaKeyFor(properties.secret.toByteArray())
    }

    fun generateAccessToken(memberId: Long, role: String): String {
        return Jwts.builder()
            .subject(memberId.toString())
            .claim("role", role)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + properties.accessTokenExpiration * 1000))
            .signWith(key)
            .compact()
    }

    fun generateRefreshToken(memberId: Long): String {
        return Jwts.builder()
            .subject(memberId.toString())
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + properties.refreshTokenExpiration * 1000))
            .signWith(key)
            .compact()
    }

    fun getMemberId(token: String): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
            .toLong()
    }

    fun getRole(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("role", String::class.java)
            ?: "STUDENT"
    }

    fun validate(token: String): Boolean {
        return runCatching {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        }.getOrDefault(false)
    }
}
