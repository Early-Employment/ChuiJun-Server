package team.joup.chuijun.domain.classroom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "학급 초대 코드 응답")
data class ClassroomInviteCodeResponse(
    @Schema(description = "학급 식별자 ID", example = "1")
    val classroomId: Long,

    @Schema(description = "생성/조회된 초대 코드 (6자리 알파벳/숫자)", example = "X7Y2WR")
    val inviteCode: String
)
