package team.joup.chuijun.domain.submission.entity

import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Embeddable
data class SolutionLikeId(
    @Column(name = "submission_id")
    val submissionId: Long,
    @Column(name = "member_id")
    val memberId: Long
) : Serializable

@Entity
@Table(name = "solution_likes")
class SolutionLikeEntity(
    @EmbeddedId
    val id: SolutionLikeId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("submissionId")
    @JoinColumn(name = "submission_id", nullable = false)
    val submission: SubmissionJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberJpaEntity,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
