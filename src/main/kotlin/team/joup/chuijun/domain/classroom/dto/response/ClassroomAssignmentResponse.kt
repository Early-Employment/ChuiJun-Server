package team.joup.chuijun.domain.classroom.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "학급 과제 응답")
data class ClassroomAssignmentResponse(
    @Schema(description = "과제 ID")
    val assignmentId: Long,

    @Schema(description = "문제 ID")
    val problemId: Long,

    @Schema(description = "문제 제목")
    val problemTitle: String,

    @Schema(description = "제한 기한")
    val dueDate: LocalDateTime,

    @Schema(description = "필수 제출 여부")
    val isRequired: Boolean,

    @Schema(description = "현재 로그인한 사용자의 제출 여부")
    val isSubmitted: Boolean
)
