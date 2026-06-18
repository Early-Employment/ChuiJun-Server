package team.joup.chuijun.domain.store.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.store.entity.ItemJpaEntity
import team.joup.chuijun.domain.store.entity.ItemType

interface ItemJpaRepository : JpaRepository<ItemJpaEntity, Long> {
    fun findByType(type: ItemType): List<ItemJpaEntity>
}
