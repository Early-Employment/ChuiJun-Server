package team.joup.chuijun.domain.classroom.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.classroom.dto.request.JoinClassroomRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomInviteCodeResponse
import team.joup.chuijun.domain.classroom.entity.ClassroomMemberJpaEntity
import team.joup.chuijun.domain.classroom.repository.ClassroomJpaRepository
import team.joup.chuijun.domain.classroom.repository.ClassroomMemberJpaRepository
import team.joup.chuijun.domain.member.entity.MemberRole
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import java.util.*

@Service
@Transactional(readOnly = true)
class ClassroomMemberService(
    private val classroomJpaRepository: ClassroomJpaRepository,
    private val classroomMemberJpaRepository: ClassroomMemberJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {

    @Transactional
    fun getOrCreateInviteCode(requestorId: Long, classroomId: Long): ClassroomInviteCodeResponse {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("초대 코드를 생성하거나 조회할 권한이 없습니다.")
        }

        if (classroom.inviteCode == null) {
            classroom.inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6).uppercase()
        }

        return ClassroomInviteCodeResponse(classroom.id!!, classroom.inviteCode!!)
    }

    @Transactional
    fun joinClassroom(studentId: Long, request: JoinClassroomRequest): Long {
        val student = memberJpaRepository.findByIdOrNull(studentId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $studentId")

        if (student.role != MemberRole.STUDENT) {
            throw IllegalArgumentException("학생 역할을 가진 회원만 학급에 가입할 수 있습니다.")
        }

        val classroom = classroomJpaRepository.findByInviteCode(request.inviteCode.uppercase())
            ?: throw NoSuchElementException("유효하지 않거나 존재하지 않는 초대 코드입니다.")

        if (classroomMemberJpaRepository.existsByClassroomIdAndStudentId(classroom.id!!, studentId)) {
            throw IllegalArgumentException("이미 이 학급에 가입된 학생입니다.")
        }

        val classroomMember = ClassroomMemberJpaEntity(
            classroom = classroom,
            student = student
        )

        return classroomMemberJpaRepository.save(classroomMember).id!!
    }

    @Transactional
    fun kickStudent(requestorId: Long, classroomId: Long, studentId: Long) {
        val classroomMember = classroomMemberJpaRepository.findAll()
            .find { it.classroom.id == classroomId && it.student.id == studentId }
            ?: throw NoSuchElementException("해당 학급에 가입되어 있지 않은 학생입니다. 학급ID: $classroomId, 학생ID: $studentId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroomMember.classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("해당 학생을 학급에서 제외할 권한이 없습니다.")
        }

        classroomMemberJpaRepository.delete(classroomMember)
    }
}
