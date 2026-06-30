package team.joup.chuijun.domain.classroom.entity

import jakarta.persistence.*
import team.joup.chuijun.domain.member.entity.MemberJpaEntity

@Entity
@Table(name = "classroom")
class ClassroomJpaEntity(
    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var grade: Int,

    @Column(nullable = false)
    var classNum: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    val teacher: MemberJpaEntity
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(name = "invite_code", nullable = true, length = 6)
    var inviteCode: String? = null
}
