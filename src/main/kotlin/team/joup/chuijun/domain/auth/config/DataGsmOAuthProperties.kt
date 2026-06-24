package team.joup.chuijun.domain.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "datagsm.oauth")
data class DataGsmOAuthProperties(
    val clientId: String = "",
    val redirectUri: String = "",
    val authorizationBaseUrl: String = "https://oauth.authorization.datagsm.kr",
    val resourceBaseUrl: String = "https://oauth.resource.datagsm.kr",
    val cookieSecure: Boolean = false,
    val accessTokenExpiration: Long = 3600
)
