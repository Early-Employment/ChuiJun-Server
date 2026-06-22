package team.joup.chuijun.domain.auth.dto

import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import team.joup.chuijun.domain.member.entity.MemberRole

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DgTokenRequest(
    val grantType: String,
    val code: String,
    val clientId: String,
    val redirectUri: String,
    val codeVerifier: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class DgTokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)

data class DgUserInfoResponse(
    val id: Long,
    val email: String,
    val role: String,
    val isStudent: Boolean,
    val student: DgStudentResponse?
)

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
