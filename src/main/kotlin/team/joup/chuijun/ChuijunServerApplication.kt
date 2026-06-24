package team.joup.chuijun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import team.joup.chuijun.domain.auth.config.DataGsmOAuthProperties
import team.joup.chuijun.global.jwt.JwtProperties

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(DataGsmOAuthProperties::class, JwtProperties::class)
class ChuijunServerApplication

fun main(args: Array<String>) {
    runApplication<ChuijunServerApplication>(*args)
}
