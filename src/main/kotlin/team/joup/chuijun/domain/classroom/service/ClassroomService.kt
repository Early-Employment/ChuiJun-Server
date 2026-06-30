package team.joup.chuijun.domain.classroom.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.classroom.dto.request.ClassroomCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomResponse
import team.joup.chuijun.domain.classroom.entity.ClassroomJpaEntity
import team.joup.chuijun.domain.classroom.repository.ClassroomJpaRepository
import team.joup.chuijun.domain.member.entity.MemberRole
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class ClassroomService(
    private val classroomJpaRepository: ClassroomJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {

    @Transactional
    fun createClassroom(teacherId: Long, request: ClassroomCreateRequest): Long {
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

    fun getClassroom(classroomId: Long): ClassroomResponse {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")
        return classroom.toResponse()
    }

    fun getClassroomsByTeacher(teacherId: Long): List<ClassroomResponse> {
        val classrooms = classroomJpaRepository.findByTeacherId(teacherId)
        return classrooms.map { it.toResponse() }
    }

    @Transactional
    fun updateClassroom(requestorId: Long, classroomId: Long, request: ClassroomUpdateRequest) {
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
    fun deleteClassroom(requestorId: Long, classroomId: Long) {
        val classroom = classroomJpaRepository.findByIdOrNull(classroomId)
            ?: throw NoSuchElementException("존재하지 않는 학급입니다. ID: $classroomId")

        val requestor = memberJpaRepository.findByIdOrNull(requestorId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $requestorId")

        if (classroom.teacher.id != requestorId && requestor.role != MemberRole.ADMIN) {
            throw IllegalArgumentException("해당 학급을 삭제할 권한이 없습니다.")
        }

        classroomJpaRepository.delete(classroom)
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
