package team.joup.chuijun.domain.classroom.dto.request

import java.time.LocalDateTime

data class ClassroomAssignmentCreateRequest(
    val problemId: Long,
    val dueDate: LocalDateTime,
    val isRequired: Boolean
)
