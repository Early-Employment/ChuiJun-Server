package team.joup.chuijun.domain.member.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.member.entity.MemberJpaEntity

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, Long> {

    fun findByEmail(email: String): MemberJpaEntity?

    fun findByStudentId(studentId: Long): MemberJpaEntity?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MemberJpaEntity m where m.id = :id")
    fun findByIdWithPessimisticLock(@Param("id") id: Long): MemberJpaEntity?
}
