package team.joup.chuijun.domain.store.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.store.dto.response.GetStoreItemResponse
import team.joup.chuijun.domain.store.entity.MemberItemJpaEntity
import team.joup.chuijun.domain.store.entity.ItemType
import team.joup.chuijun.domain.store.repository.ItemJpaRepository
import team.joup.chuijun.domain.store.repository.MemberItemJpaRepository
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class StoreService(
    private val itemJpaRepository: ItemJpaRepository,
    private val memberItemJpaRepository: MemberItemJpaRepository,
    private val memberJpaRepository: MemberJpaRepository
) {

    fun getStoreItems(memberId: Long, type: ItemType?): List<GetStoreItemResponse> {
        val items = if (type != null) {
            itemJpaRepository.findByType(type)
        } else {
            itemJpaRepository.findAll()
        }

        val ownedItemIds = memberItemJpaRepository.findByMemberId(memberId)
            .map { it.item.id }
            .toSet()

        return items.map { item ->
            GetStoreItemResponse(
                itemId = checkNotNull(item.id),
                name = item.name,
                description = item.description,
                price = item.price,
                type = item.type,
                isOwned = ownedItemIds.contains(item.id)
            )
        }
    }

    @Transactional
    fun purchaseItem(memberId: Long, itemId: Long) {
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다.")

        val item = itemJpaRepository.findByIdOrNull(itemId)
            ?: throw NoSuchElementException("존재하지 않는 상점 아이템입니다.")

        if (memberItemJpaRepository.existsByMemberIdAndItemId(memberId, itemId)) {
            throw IllegalStateException("이미 보유하고 있는 아이템입니다.")
        }

        if (member.coin < item.price) {
            throw IllegalStateException("코인이 부족합니다. 보유 코인: ${member.coin}, 아이템 가격: ${item.price}")
        }

        memberItemJpaRepository.save(
            MemberItemJpaEntity(
                member = member,
                item = item
            )
        )
    }
}
