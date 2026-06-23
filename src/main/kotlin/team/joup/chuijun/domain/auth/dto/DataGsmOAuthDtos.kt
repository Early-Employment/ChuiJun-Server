package team.joup.chuijun.domain.auth.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import team.joup.chuijun.domain.member.entity.MemberRole

// DataGSM 토큰 엔드포인트는 snake_case JSON 을 주고받는다. 클래스패스에 Jackson 2 와 3 이 함께 있지만
// 런타임 직렬화는 Jackson 3(tools.jackson, jackson-module-kotlin 3)이 담당한다. DTO 에 붙은 @JsonNaming 은
// Jackson 2(com.fasterxml.jackson.databind) 어노테이션이라 Jackson 3 매퍼가 인식하지 못해 무시된다.
// 그래서 네이밍 전략에 의존하지 않고 각 필드에 @JsonProperty(Jackson 3 도 인식)를 명시한다.
data class DgTokenRequest(
    @JsonProperty("grant_type") val grantType: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("redirect_uri") val redirectUri: String,
    @JsonProperty("code_verifier") val codeVerifier: String
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DgTokenResponse(
    @JsonProperty("access_token") val accessToken: String,
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("expires_in") val expiresIn: Int
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DgUserInfoResponse(
    val id: Long,
    val email: String,
    val role: String,
    val isStudent: Boolean,
    val student: DgStudentResponse?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DgStudentResponse(
    val id: Long,
    val name: String,
    val sex: String?,
    val grade: Int?,
    val classNum: Int?,
    val number: Int?,
    val studentNumber: Int?,
    val major: String?,
    val specialty: String?,
    val dormitoryFloor: Int?,
    val dormitoryRoom: Int?,
    val role: String?,
    val isLeaveSchool: Boolean?
)

data class DgLoginResponse(
    val memberId: Long,
    val email: String,
    val name: String,
    val role: MemberRole,
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)
