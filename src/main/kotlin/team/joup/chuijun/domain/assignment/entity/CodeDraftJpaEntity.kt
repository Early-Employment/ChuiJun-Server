package team.joup.chuijun.domain.assignment.entity

import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "code_drafts")
class CodeDraftJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    var problem: ProblemJpaEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    var assignment: AssignmentJpaEntity? = null,

    @Column(name = "language_code", nullable = false, length = 40)
    var languageCode: String,

    @Column(name = "draft_code", nullable = false, columnDefinition = "TEXT")
    var draftCode: String,

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
