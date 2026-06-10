package team.joup.chuijun.domain.member.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "members")
class MemberJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "course_id")
    var courseId: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: MemberRole = MemberRole.STUDENT,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(nullable = false, length = 80)
    var name: String,

    @Column(name = "profile_image_url", length = 500)
    var profileImageUrl: String? = null,

    @Column(name = "student_id", unique = true)
    val studentId: Long? = null,

    var grade: Int? = null,

    @Column(name = "class_num")
    var classNum: Int? = null,

    var number: Int? = null,

    var major: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var tier: MemberTier = MemberTier.BRONZE,

    @Column(nullable = false)
    var rating: Int = 0,

    @Column(name = "badges_json", columnDefinition = "JSON")
    var badgesJson: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
