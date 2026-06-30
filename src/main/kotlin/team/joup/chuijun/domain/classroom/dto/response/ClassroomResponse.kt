package team.joup.chuijun.domain.classroom.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "학급 정보 응답")
data class ClassroomResponse(
    @Schema(description = "학급 식별자 ID", example = "1")
    val id: Long,

    @Schema(description = "수업 이름", example = "알고리즘 (3-1)")
    val name: String,

    @Schema(description = "학년", example = "3")
    val grade: Int,

    @Schema(description = "반", example = "1")
    val classNum: Int,

    @Schema(description = "담당 선생님 이름", example = "홍길동")
    val teacherName: String
)
