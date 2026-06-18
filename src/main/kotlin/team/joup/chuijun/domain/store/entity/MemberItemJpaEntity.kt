package team.joup.chuijun.domain.store.entity

import jakarta.persistence.*
import team.joup.chuijun.domain.member.entity.MemberJpaEntity

@Entity
@Table(name = "member_items")
class MemberItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val item: ItemJpaEntity
)
