package team.joup.chuijun.domain.auth.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import team.joup.chuijun.domain.auth.config.DataGsmOAuthProperties
import team.joup.chuijun.domain.auth.dto.DgLoginResponse
import team.joup.chuijun.domain.auth.service.DataGsmOAuthService
import java.time.Duration

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/auth/dg")
class DataGsmAuthController(
    private val dataGsmOAuthService: DataGsmOAuthService,
    private val properties: DataGsmOAuthProperties
) {

    @Operation(summary = "DataGSM 로그인 시작", description = "PKCE state/verifier를 쿠키에 저장하고 DataGSM OAuth 인증 페이지로 이동합니다.")
    @GetMapping("/login")
    fun login(): ResponseEntity<Void> {
        val authorizationRequest = dataGsmOAuthService.createAuthorizationRequest()

        return ResponseEntity.status(HttpStatus.FOUND)
            .location(authorizationRequest.url)
            .header(HttpHeaders.SET_COOKIE, oauthCookie(STATE_COOKIE, authorizationRequest.state).toString())
            .header(HttpHeaders.SET_COOKIE, oauthCookie(CODE_VERIFIER_COOKIE, authorizationRequest.codeVerifier).toString())
            .build()
    }

    @Operation(summary = "DataGSM 로그인 콜백", description = "Authorization Code를 토큰으로 교환하고 DataGSM 사용자 정보로 회원을 생성 또는 갱신합니다.")
    @GetMapping("/callback")
    fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        @CookieValue(name = STATE_COOKIE, required = false) savedState: String?,
        @CookieValue(name = CODE_VERIFIER_COOKIE, required = false) codeVerifier: String?
    ): ResponseEntity<DgLoginResponse> {
        val response = dataGsmOAuthService.login(
            code = code,
            state = state,
            savedState = savedState,
            codeVerifier = codeVerifier
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearCookie(STATE_COOKIE).toString())
            .header(HttpHeaders.SET_COOKIE, clearCookie(CODE_VERIFIER_COOKIE).toString())
            .body(response)
    }

    private fun oauthCookie(name: String, value: String): ResponseCookie {
        return ResponseCookie.from(name, value)
            .httpOnly(true)
            .secure(properties.cookieSecure)
            .sameSite("Lax")
            .path("/auth/dg")
            .maxAge(Duration.ofMinutes(5))
            .build()
    }

    private fun clearCookie(name: String): ResponseCookie {
        return ResponseCookie.from(name, "")
            .httpOnly(true)
            .secure(properties.cookieSecure)
            .sameSite("Lax")
            .path("/auth/dg")
            .maxAge(Duration.ZERO)
            .build()
    }

    private companion object {
        const val STATE_COOKIE = "dg_oauth_state"
        const val CODE_VERIFIER_COOKIE = "dg_oauth_code_verifier"
    }
}
