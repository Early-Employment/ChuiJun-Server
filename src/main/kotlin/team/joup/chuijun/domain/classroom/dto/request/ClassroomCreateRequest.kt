package team.joup.chuijun.domain.classroom.dto.request

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "학급 생성 요청")
data class ClassroomCreateRequest(
    @Schema(description = "수업 이름", example = "알고리즘 (3-1)")
    val name: String,

    @Schema(description = "학년", example = "3")
    val grade: Int,

    @Schema(description = "반", example = "1")
    val classNum: Int
)
