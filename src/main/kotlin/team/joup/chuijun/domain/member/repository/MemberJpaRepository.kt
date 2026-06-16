package team.joup.chuijun.domain.member.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.member.entity.MemberJpaEntity

interface MemberJpaRepository : JpaRepository<MemberJpaEntity, Long> {
    fun findAllByOrderByRatingDesc(pageable: Pageable): Page<MemberJpaEntity>
}
