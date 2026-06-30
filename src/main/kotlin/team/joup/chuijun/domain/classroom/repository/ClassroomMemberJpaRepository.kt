package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import team.joup.chuijun.domain.classroom.entity.ClassroomMemberJpaEntity

@Repository
interface ClassroomMemberJpaRepository : JpaRepository<ClassroomMemberJpaEntity, Long> {
    fun existsByClassroomIdAndStudentId(classroomId: Long, studentId: Long): Boolean
    fun findByClassroomIdAndStudentId(classroomId: Long, studentId: Long): ClassroomMemberJpaEntity?
    fun findByClassroomId(classroomId: Long): List<ClassroomMemberJpaEntity>
}
