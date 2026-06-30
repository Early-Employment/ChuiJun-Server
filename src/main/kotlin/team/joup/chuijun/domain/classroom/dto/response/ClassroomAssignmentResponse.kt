package team.joup.chuijun.domain.classroom.dto.response

import java.time.LocalDateTime

data class ClassroomAssignmentResponse(
    val assignmentId: Long,
    val problemId: Long,
    val problemTitle: String,
    val dueDate: LocalDateTime,
    val isRequired: Boolean
)
