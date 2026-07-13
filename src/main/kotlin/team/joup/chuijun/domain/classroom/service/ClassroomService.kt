package team.joup.chuijun.domain.classroom.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.classroom.dto.request.ClassroomCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.*
import team.joup.chuijun.domain.classroom.entity.ClassroomJpaEntity
import team.joup.chuijun.domain.classroom.repository.ClassroomAssignmentJpaRepository
import team.joup.chuijun.domain.classroom.repository.ClassroomJpaRepository
import team.joup.chuijun.domain.classroom.repository.ClassroomMemberJpaRepository
import team.joup.chuijun.domain.member.entity.MemberRole
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
open class ClassroomService(
    private val classroomJpaRepository: ClassroomJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val classroomMemberJpaRepository: ClassroomMemberJpaRepository,
    private val assignmentJpaRepository: ClassroomAssignmentJpaRepository,
    private val submissionJpaRepository: SubmissionJpaRepository
) {

    @Transactional
    open fun createClassroom(teacherId: Long, request: ClassroomCreateRequest): Long {
        val teacher = memberJpaRepository.findByIdOrNull(teacherId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $teacherId")

        if (teacher.role == MemberRole.STUDENT) {
            throw IllegalArgumentException("학생은 학급을 개설할 수 없습니다.")
        }

        val classroom = ClassroomJpaEntity(
            name = request.name,
            grade = request.grade,
            classNum = request.classNum,
            teacher = teacher
        )
        return classroomJpaRepository.save(classroom).id!!
    }

    open fun getClassroom(classroomId: Long): ClassroomResponse {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")
        return classroom.toResponse()
    }

    open fun getClassroomsByTeacher(teacherId: Long): List<ClassroomResponse> {
        val requestor = memberJpaRepository.findByIdOrNull(teacherId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $teacherId")

        if (requestor.role != MemberRole.TEACHER && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("선생님 혹은 관리자 권한이 필요합니다.")
        }

        val classrooms = classroomJpaRepository.findAll()

        return classrooms.map { it.toResponse() }
    }

    @Transactional
    open fun updateClassroom(requestorId: Long, classroomId: Long, request: ClassroomUpdateRequest) {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("해당 학급의 정보를 수정할 권한이 없습니다.")
        }

        classroom.name = request.name
        classroom.grade = request.grade
        classroom.classNum = request.classNum
    }

    @Transactional
    open fun deleteClassroom(requestorId: Long, classroomId: Long) {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("해당 학급을 삭제할 권한이 없습니다.")
        }

        classroomJpaRepository.delete(classroom)
    }

    open fun getTeacherDashboard(requestorId: Long, classroomId: Long): ClassroomTeacherDashboardResponse {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (requestor.role != MemberRole.TEACHER && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("선생님 권한이 없는 사용자는 대시보드를 조회할 수 없습니다.")
        }

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("본인이 담당하는 학급의 대시보드만 조회할 수 있습니다.")
        }

        val classroomMembers = classroomMemberJpaRepository.findByClassroomIdWithStudent(classroomId)
        val studentIds = classroomMembers.map { it.student.id!! }

        val students = classroomMembers.map {
            ClassroomStudentResponse(
                memberId = it.student.id!!,
                name = it.student.name,
                profileImageUrl = it.student.profileImageUrl
            )
        }

        val assignments = assignmentJpaRepository.findByClassroomId(classroomId)
        val problemIds = assignments.map { it.problem.id!! }

        if (studentIds.isEmpty() || problemIds.isEmpty()) {
            return ClassroomTeacherDashboardResponse(
                stats = ClassroomStatsResponse(0, 0, 0, 0),
                students = students,
                assignments = emptyList()
            )
        }

        val allSubmissions = submissionJpaRepository.findByMemberIdInAndProblemIdIn(studentIds, problemIds)

        val submittedPairs = allSubmissions.mapNotNull { sub ->
            val pId = sub.problem?.id
            if (pId != null) sub.member.id!! to pId else null
        }.toSet()

        val totalPossibleSubmissions = studentIds.size * problemIds.size
        val actualSubmittedCount = studentIds.sumOf { studentId ->
            problemIds.count { problemId -> submittedPairs.contains(studentId to problemId) }
        }
        val totalSubmissionRate = if (totalPossibleSubmissions > 0) {
            (actualSubmittedCount * 100) / totalPossibleSubmissions
        } else 0

        val latestAssignment = assignments.maxByOrNull { it.dueDate }
        val recentMissingStudentsCount = if (latestAssignment != null) {
            val latestProblemId = latestAssignment.problem.id!!
            studentIds.count { studentId -> !submittedPairs.contains(studentId to latestProblemId) }
        } else 0

        val acceptedSubmissions = allSubmissions.filter { it.judgeStatus == JudgeStatus.PASSED || it.judgeStatus == JudgeStatus.AC }
        val averageCorrectRate = if (allSubmissions.isNotEmpty()) {
            (acceptedSubmissions.size * 100) / allSubmissions.size
        } else 0

        val submissionsGroupedByProblem = allSubmissions.groupBy { it.problem?.id }

        val lowCorrectRateProblemCount = problemIds.count { problemId ->
            val problemSubmissions = submissionsGroupedByProblem[problemId] ?: emptyList()
            if (problemSubmissions.isNotEmpty()) {
                val problemAcceptedCount = problemSubmissions.count { it.judgeStatus == JudgeStatus.PASSED || it.judgeStatus == JudgeStatus.AC }
                val rate = (problemAcceptedCount * 100) / problemSubmissions.size
                rate <= 50
            } else false
        }

        val assignmentResponses = assignments.map { assignment ->
            val pId = assignment.problem.id!!
            val submittedCount = studentIds.count { studentId -> submittedPairs.contains(studentId to pId) }
            TeacherAssignmentResponse(
                assignmentId = assignment.id!!,
                problemTitle = assignment.problem.title,
                submissionStatusText = "제출 : $submittedCount / ${studentIds.size}"
            )
        }

        val stats = ClassroomStatsResponse(
            totalSubmissionRate = totalSubmissionRate,
            recentMissingStudentsCount = recentMissingStudentsCount,
            averageCorrectRate = averageCorrectRate,
            lowCorrectRateProblemCount = lowCorrectRateProblemCount
        )

        return ClassroomTeacherDashboardResponse(
            stats = stats,
            students = students,
            assignments = assignmentResponses
        )
    }

    private fun ClassroomJpaEntity.toResponse(): ClassroomResponse {
        return ClassroomResponse(
            id = this.id!!,
            name = this.name,
            grade = this.grade,
            classNum = this.classNum,
            teacherName = this.teacher.name
        )
    }
}
