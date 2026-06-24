package team.joup.chuijun.global.jwt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String = "",
    val accessTokenExpiration: Long = 3600,
    val refreshTokenExpiration: Long = 2592000
)
