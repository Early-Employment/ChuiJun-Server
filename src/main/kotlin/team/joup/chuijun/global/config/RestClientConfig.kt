package team.joup.chuijun.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient
import java.net.http.HttpClient
import java.time.Duration

@Configuration
class RestClientConfig {

    @Bean
    fun restClient(): RestClient {
        // 타임아웃이 없으면 DataGSM 등 업스트림이 응답하지 않을 때 요청이 무한정 매달려
        // 콜백이 30초+ 동안 행 걸린 뒤 의미 없는 500을 던진다. 빠르게 실패시켜 원인을 분류한다.
        // 커넥션 풀링과 HTTP/2 를 지원하는 java.net.http.HttpClient 기반 팩토리를 사용한다.
        val httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build()
        val requestFactory = JdkClientHttpRequestFactory(httpClient).apply {
            setReadTimeout(Duration.ofSeconds(10))
        }

        return RestClient.builder()
            .requestFactory(requestFactory)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }
}
