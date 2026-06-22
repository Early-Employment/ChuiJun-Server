package team.joup.chuijun.domain.auth.dto

import team.joup.chuijun.domain.member.entity.MemberRole

data class DgTokenRequest(
    val grant_type: String,
    val code: String,
    val client_id: String,
    val redirect_uri: String,
    val code_verifier: String
)

data class DgTokenResponse(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int
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
