package team.joup.chuijun.domain.classroom.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.classroom.dto.request.ClassroomAssignmentCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomAssignmentUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomAssignmentResponse
import team.joup.chuijun.domain.classroom.entity.ClassroomAssignmentJpaEntity
import team.joup.chuijun.domain.classroom.entity.ClassroomMemberJpaEntity
import team.joup.chuijun.domain.classroom.repository.ClassroomAssignmentJpaRepository
import team.joup.chuijun.domain.classroom.repository.ClassroomJpaRepository
import team.joup.chuijun.domain.classroom.repository.ClassroomMemberJpaRepository
import team.joup.chuijun.domain.member.entity.MemberRole
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.problem.repository.ProblemJpaRepository
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class ClassroomAssignmentService(
    private val assignmentJpaRepository: ClassroomAssignmentJpaRepository,
    private val classroomJpaRepository: ClassroomJpaRepository,
    private val problemJpaRepository: ProblemJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val classroomMemberJpaRepository: ClassroomMemberJpaRepository
) {

    @Transactional
    fun assignClassroomAutomatically(memberId: Long) {
        val student = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        val grade = student.grade ?: return
        val classNum = student.classNum ?: return

        val matchedClassrooms = classroomJpaRepository.findByGradeAndClassNum(grade, classNum)
        val matchedClassroom = matchedClassrooms.firstOrNull() ?: return

        val classroomId = checkNotNull(matchedClassroom.id)
        val studentId = checkNotNull(student.id)

        classroomMemberJpaRepository.deleteByStudentId(studentId)

        classroomMemberJpaRepository.save(
            ClassroomMemberJpaEntity(
                classroom = matchedClassroom,
                student = student
            )
        )
    }

    @Transactional
    fun assignProblem(requestorId: Long, classroomId: Long, request: ClassroomAssignmentCreateRequest): Long {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("이 학급에 과제를 출제할 권한이 없습니다.")
        }

        val problem = problemJpaRepository.findByIdOrNull(request.problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: ${request.problemId}")

        val assignment = ClassroomAssignmentJpaEntity(
            classroom = classroom,
            problem = problem,
            dueDate = request.dueDate,
            isRequired = request.isRequired
        )
        return assignmentJpaRepository.save(assignment).id!!
    }

    fun getAssignmentsByClassroom(classroomId: Long): List<ClassroomAssignmentResponse> {
        val assignments = assignmentJpaRepository.findByClassroomId(classroomId)
        return assignments.map {
            ClassroomAssignmentResponse(
                assignmentId = it.id!!,
                problemId = it.problem.id!!,
                problemTitle = it.problem.title,
                dueDate = it.dueDate,
                isRequired = it.isRequired
            )
        }
    }

    @Transactional
    fun updateAssignment(requestorId: Long, assignmentId: Long, request: ClassroomAssignmentUpdateRequest) {
        val assignment = assignmentJpaRepository.findByIdOrNull(assignmentId)
            ?: throw NoSuchElementException("존재하지 않는 과제입니다. ID: $assignmentId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (assignment.classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("이 과제를 수정할 권한이 없습니다.")
        }

        assignment.dueDate = request.dueDate
        assignment.isRequired = request.isRequired
    }

    @Transactional
    fun deleteAssignment(requestorId: Long, assignmentId: Long) {
        val assignment = assignmentJpaRepository.findByIdOrNull(assignmentId)
            ?: throw NoSuchElementException("존재하지 않는 과제입니다. ID: $assignmentId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (assignment.classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("이 과제를 삭제할 권한이 없습니다.")
        }

        assignmentJpaRepository.delete(assignment)
    }
}
