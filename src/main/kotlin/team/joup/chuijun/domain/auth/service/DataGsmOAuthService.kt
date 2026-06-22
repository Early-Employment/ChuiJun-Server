package team.joup.chuijun.domain.auth.service

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import team.joup.chuijun.domain.auth.config.DataGsmOAuthProperties
import team.joup.chuijun.domain.auth.dto.DgLoginResponse
import team.joup.chuijun.domain.auth.dto.DgTokenRequest
import team.joup.chuijun.domain.auth.dto.DgTokenResponse
import team.joup.chuijun.domain.auth.dto.DgUserInfoResponse
import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import team.joup.chuijun.domain.member.entity.MemberRole
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import java.net.URI
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.util.Base64

@Service
class DataGsmOAuthService(
    private val properties: DataGsmOAuthProperties,
    private val restClient: RestClient,
    private val persistenceService: DataGsmPersistenceService
) {

    private val secureRandom = SecureRandom()

    fun createAuthorizationRequest(serverBaseUrl: String): AuthorizationRequest {
        val clientId = properties.clientId.validConfigValue("DATAGSM_CLIENT_ID")
        val redirectUri = properties.redirectUri
            .takeIf { it.isUsableConfigValue() }
            ?: "$serverBaseUrl/auth/dg/callback"

        val state = randomUrlSafeString(32)
        val codeVerifier = randomUrlSafeString(64)
        val codeChallenge = createCodeChallenge(codeVerifier)

        val url = UriComponentsBuilder
            .fromUriString("${properties.authorizationBaseUrl}/v1/oauth/authorize")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("state", state)
            .queryParam("code_challenge", codeChallenge)
            .queryParam("code_challenge_method", "S256")
            .apply {
                if (properties.scope.isNotBlank()) {
                    queryParam("scope", properties.scope)
                }
            }
            .build()
            .encode()
            .toUri()

        return AuthorizationRequest(url, state, codeVerifier, redirectUri)
    }

    fun login(code: String, state: String, savedState: String?, codeVerifier: String?, redirectUri: String?): DgLoginResponse {
        require(savedState != null && codeVerifier != null) { "OAuth 인증 쿠키가 없습니다. 로그인을 다시 시작해 주세요." }
        require(state == savedState) { "OAuth state 값이 일치하지 않습니다." }
        val resolvedRedirectUri = redirectUri.validConfigValue("OAuth redirect_uri")

        val token = exchangeToken(code, codeVerifier, resolvedRedirectUri)
        val userInfo = getUserInfo(token.accessToken)
        val member = persistenceService.upsertMember(userInfo)

        return DgLoginResponse(
            memberId = checkNotNull(member.id) { "회원 데이터의 식별자가 누락되었습니다." },
            email = member.email,
            name = member.name,
            role = member.role,
            accessToken = token.accessToken,
            refreshToken = token.refreshToken,
            expiresIn = token.expiresIn
        )
    }

    private fun exchangeToken(code: String, codeVerifier: String, redirectUri: String): DgTokenResponse {
        val request = DgTokenRequest(
            grantType = "authorization_code",
            code = code,
            clientId = properties.clientId.validConfigValue("DATAGSM_CLIENT_ID"),
            redirectUri = redirectUri,
            codeVerifier = codeVerifier
        )

        return restClient.post()
            .uri("${properties.authorizationBaseUrl}/v1/oauth/token")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(DgTokenResponse::class.java)
            ?: throw IllegalStateException("DataGSM 토큰 응답이 비어 있습니다.")
    }

    private fun getUserInfo(accessToken: String): DgUserInfoResponse {
        return restClient.get()
            .uri("${properties.resourceBaseUrl}/userinfo")
            .header("Authorization", "Bearer $accessToken")
            .retrieve()
            .body(DgUserInfoResponse::class.java)
            ?: throw IllegalStateException("DataGSM 사용자 정보 응답이 비어 있습니다.")
    }

    private fun createCodeChallenge(codeVerifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(codeVerifier.toByteArray(Charsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    private fun randomUrlSafeString(byteLength: Int): String {
        val bytes = ByteArray(byteLength)
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    data class AuthorizationRequest(
        val url: URI,
        val state: String,
        val codeVerifier: String,
        val redirectUri: String
    )
}

private fun String?.isUsableConfigValue(): Boolean {
    return !isNullOrBlank() && !contains("\${")
}

private fun String?.validConfigValue(name: String): String {
    require(isUsableConfigValue()) { "$name 환경 변수가 실제 값으로 설정되지 않았습니다." }
    return checkNotNull(this)
}

@Component
class DataGsmPersistenceService(
    private val memberJpaRepository: MemberJpaRepository
) {

    @Transactional
    fun upsertMember(userInfo: DgUserInfoResponse): MemberJpaEntity {
        val student = userInfo.student
        val existingMember = student?.id?.let { memberJpaRepository.findByStudentId(it) }
            ?: memberJpaRepository.findByEmail(userInfo.email)

        val now = LocalDateTime.now()
        val name = student?.name ?: userInfo.email.substringBefore("@")

        val member = existingMember ?: MemberJpaEntity(
            email = userInfo.email,
            name = name,
            role = resolveMemberRole(userInfo.role, student?.role),
            studentId = student?.id
        )

        member.email = userInfo.email
        member.name = name
        member.role = resolveMemberRole(userInfo.role, student?.role)
        member.studentId = student?.id ?: member.studentId
        member.grade = student?.grade
        member.classNum = student?.classNum
        member.number = student?.number
        member.major = student?.major
        member.updatedAt = now

        return memberJpaRepository.save(member)
    }

    private fun resolveMemberRole(userRole: String, studentRole: String?): MemberRole {
        val roles = listOf(userRole, studentRole).filterNotNull()
        return when {
            roles.any { it.contains("ADMIN", ignoreCase = true) } -> MemberRole.ADMIN
            roles.any { it.contains("TEACHER", ignoreCase = true) } -> MemberRole.TEACHER
            else -> MemberRole.STUDENT
        }
    }
}
