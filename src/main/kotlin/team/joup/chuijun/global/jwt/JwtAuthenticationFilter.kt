package team.joup.chuijun.global.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Authorization 헤더의 Bearer Access Token 을 검증해 SecurityContext 에 인증을 채운다.
 * 컨트롤러의 @AuthenticationPrincipal 은 이 필터가 채운 principal(username = memberId)을 받는다.
 * 토큰이 없거나 무효이면 컨텍스트를 채우지 않고 통과시키며, 보호 엔드포인트가 401 을 응답한다.
 */
@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)
        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            // 토큰 검증·파싱을 한 번만 수행한다(무효/만료면 null).
            val claims = jwtProvider.getClaims(token)
            if (claims != null) {
                val role = claims.get("role", String::class.java) ?: "STUDENT"
                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))
                val principal = User(claims.subject, "", authorities)
                val authentication = UsernamePasswordAuthenticationToken(principal, null, authorities)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return if (header.startsWith(BEARER_PREFIX)) header.substring(BEARER_PREFIX.length) else null
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
