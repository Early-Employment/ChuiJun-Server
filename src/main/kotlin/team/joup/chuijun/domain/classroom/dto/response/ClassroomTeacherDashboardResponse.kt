package team.joup.chuijun.domain.classroom.dto.response

data class ClassroomTeacherDashboardResponse(
    val stats: ClassroomStatsResponse,
    val students: List<ClassroomStudentResponse>,
    val assignments: List<TeacherAssignmentResponse>
)

data class ClassroomStatsResponse(
    val totalSubmissionRate: Int,
    val recentMissingStudentsCount: Int,
    val averageCorrectRate: Int,
    val lowCorrectRateProblemCount: Int
)

data class ClassroomStudentResponse(
    val memberId: Long,
    val name: String,
    val profileImageUrl: String?
)

data class TeacherAssignmentResponse(
    val assignmentId: Long,
    val problemTitle: String,
    val submissionStatusText: String
)
