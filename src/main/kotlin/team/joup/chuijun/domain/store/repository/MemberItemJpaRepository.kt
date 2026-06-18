package team.joup.chuijun.domain.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.store.entity.MemberItemJpaEntity

interface MemberItemJpaRepository : JpaRepository<MemberItemJpaEntity, Long> {
    fun findByMemberId(memberId: Long): List<MemberItemJpaEntity>
    fun existsByMemberIdAndItemId(memberId: Long, itemId: Long): Boolean
}
