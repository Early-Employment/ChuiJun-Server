package team.joup.chuijun.domain.assignment.entity

import team.joup.chuijun.domain.member.entity.MemberEntity
import team.joup.chuijun.domain.problem.entity.ProblemEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "code_drafts")
class CodeDraftEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: MemberEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    var problem: ProblemEntity? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id")
    var assignment: AssignmentEntity? = null,

    @Column(name = "language_code", nullable = false, length = 40)
    var languageCode: String,

    @Column(name = "draft_code", nullable = false, columnDefinition = "MEDIUMTEXT")
    var draftCode: String,

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null,

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
