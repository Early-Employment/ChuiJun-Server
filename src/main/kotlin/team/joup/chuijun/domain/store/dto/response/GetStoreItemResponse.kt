package team.joup.chuijun.domain.store.dto.response

import team.joup.chuijun.domain.store.entity.ItemType

data class GetStoreItemResponse(
    val itemId: Long,
    val name: String,
    val description: String,
    val price: Int,
    val type: ItemType,
    val isOwned: Boolean
)
