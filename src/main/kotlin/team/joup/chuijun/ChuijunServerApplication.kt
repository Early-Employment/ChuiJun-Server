package team.joup.chuijun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChuijunServerApplication

fun main(args: Array<String>) {
    runApplication<ChuijunServerApplication>(*args)
}
