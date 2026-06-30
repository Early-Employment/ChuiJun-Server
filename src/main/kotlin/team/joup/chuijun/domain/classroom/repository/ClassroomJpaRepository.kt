package team.joup.chuijun.domain.classroom.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.classroom.entity.ClassroomJpaEntity

interface ClassroomJpaRepository : JpaRepository<ClassroomJpaEntity, Long> {
    fun findByInviteCode(inviteCode: String): ClassroomJpaEntity?
}
