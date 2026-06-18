package team.joup.chuijun.domain.store.entity

import jakarta.persistence.*

@Entity
@Table(name = "items")
class ItemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val price: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: ItemType
)
