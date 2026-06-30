package team.joup.chuijun.domain.classroom.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "학급 정보 수정 요청")
data class ClassroomUpdateRequest(
    @Schema(description = "수업 이름", example = "자료구조 (3-1)")
    val name: String,

    @Schema(description = "학년", example = "3")
    val grade: Int,

    @Schema(description = "반", example = "1")
    val classNum: Int
)
