package team.joup.chuijun.domain.auth.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import team.joup.chuijun.domain.member.entity.MemberRole

data class DgTokenRequest(
    @JsonProperty("grant_type") val grantType: String,
    @JsonProperty("code") val code: String,
    @JsonProperty("client_id") val clientId: String,
    @JsonProperty("redirect_uri") val redirectUri: String,
    @JsonProperty("code_verifier") val codeVerifier: String
)

data class DgTokenRefreshRequest(
    @JsonProperty("grant_type") val grantType: String = "refresh_token",
    @JsonProperty("refresh_token") val refreshToken: String,
    @JsonProperty("client_id") val clientId: String
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

data class DgRefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)
