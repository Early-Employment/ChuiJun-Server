package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.classroom.entity.ClassroomMemberJpaEntity

interface ClassroomMemberJpaRepository : JpaRepository<ClassroomMemberJpaEntity, Long> {
    fun existsByClassroomIdAndStudentId(classroomId: Long, studentId: Long): Boolean

    fun findByClassroomId(classroomId: Long): List<ClassroomMemberJpaEntity>
}
