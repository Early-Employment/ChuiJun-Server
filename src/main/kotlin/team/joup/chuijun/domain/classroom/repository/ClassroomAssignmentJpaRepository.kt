package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.classroom.entity.ClassroomAssignmentJpaEntity

interface ClassroomAssignmentJpaRepository : JpaRepository<ClassroomAssignmentJpaEntity, Long> {

    @Query("SELECT a FROM ClassroomAssignmentJpaEntity a JOIN FETCH a.problem WHERE a.classroom.id = :classroomId")
    fun findByClassroomId(@Param("classroomId") classroomId: Long): List<ClassroomAssignmentJpaEntity>
}
