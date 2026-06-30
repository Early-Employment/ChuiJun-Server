package team.joup.chuijun.domain.classroom.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "초대 코드를 통한 학급 가입 요청")
data class JoinClassroomRequest(
    @Schema(description = "선생님에게 받은 학급 초대 코드", example = "X7Y2WR")
    val inviteCode: String
)
