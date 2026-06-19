package team.joup.chuijun.domain.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.store.entity.MemberItemJpaEntity

interface MemberItemJpaRepository : JpaRepository<MemberItemJpaEntity, Long> {

    @Query("select mi.item.id from MemberItemJpaEntity mi where mi.member.id = :memberId")
    fun findItemIdsByMemberId(@Param("memberId") memberId: Long): List<Long>

    fun existsByMemberIdAndItemId(memberId: Long, itemId: Long): Boolean
}
