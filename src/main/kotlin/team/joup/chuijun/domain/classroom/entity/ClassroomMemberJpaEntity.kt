package team.joup.chuijun.domain.classroom.entity

import jakarta.persistence.*
import team.joup.chuijun.domain.member.entity.MemberJpaEntity

@Entity
@Table(
    name = "classroom_member",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["classroom_id", "member_id"])
    ]
)
class ClassroomMemberJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    val classroom: ClassroomJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val student: MemberJpaEntity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
