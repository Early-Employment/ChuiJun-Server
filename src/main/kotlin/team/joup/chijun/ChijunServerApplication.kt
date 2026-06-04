package team.joup.chijun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ChijunServerApplication

fun main(args: Array<String>) {
    runApplication<ChijunServerApplication>(*args)
}
