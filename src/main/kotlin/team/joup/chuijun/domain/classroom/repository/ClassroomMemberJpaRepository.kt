package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import team.joup.chuijun.domain.classroom.entity.ClassroomMemberJpaEntity

@Repository
interface ClassroomMemberJpaRepository : JpaRepository<ClassroomMemberJpaEntity, Long> {
    fun existsByClassroomIdAndStudentId(classroomId: Long, studentId: Long): Boolean
    fun findByClassroomIdAndStudentId(classroomId: Long, studentId: Long): ClassroomMemberJpaEntity?
    fun findByClassroomId(classroomId: Long): List<ClassroomMemberJpaEntity>
    fun deleteByStudentId(studentId: Long)

    @Query("SELECT cm FROM ClassroomMemberJpaEntity cm JOIN FETCH cm.classroom c JOIN FETCH c.teacher WHERE cm.student.id = :studentId")
    fun findByStudentIdWithClassroom(@Param("studentId") studentId: Long): List<ClassroomMemberJpaEntity>

    @Query("SELECT cm FROM ClassroomMemberJpaEntity cm JOIN FETCH cm.student WHERE cm.classroom.id = :classroomId")
    fun findByClassroomIdWithStudent(@Param("classroomId") classroomId: Long): List<ClassroomMemberJpaEntity>
}
