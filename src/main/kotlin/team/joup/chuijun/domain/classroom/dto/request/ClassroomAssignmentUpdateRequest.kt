package team.joup.chuijun.domain.classroom.dto.request

import java.time.LocalDateTime

data class ClassroomAssignmentUpdateRequest(
    val dueDate: LocalDateTime,
    val isRequired: Boolean
)
