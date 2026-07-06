package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import team.joup.chuijun.domain.classroom.entity.ClassroomJpaEntity

@Repository
interface ClassroomJpaRepository : JpaRepository<ClassroomJpaEntity, Long> {
    fun findByInviteCode(inviteCode: String): ClassroomJpaEntity?
    fun findByTeacherId(teacherId: Long): List<ClassroomJpaEntity>
    fun findByGradeAndClassNum(grade: Int, classNum: Int): ClassroomJpaEntity?
}
