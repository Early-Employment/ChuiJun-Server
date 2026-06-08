package team.joup.chuijun.domain.submission.entity

import team.joup.chuijun.domain.member.entity.MemberEntity
import team.joup.chuijun.domain.problem.entity.ProblemEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "comments")
class CommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_type", nullable = false)
    var commentType: CommentType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: ProblemEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id")
    var submission: SubmissionEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    var writer: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    var receiver: MemberEntity? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "ai_review_status", nullable = false)
    var aiReviewStatus: AiReviewStatus = AiReviewStatus.NOT_CHECKED,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: CommentStatus = CommentStatus.VISIBLE,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
