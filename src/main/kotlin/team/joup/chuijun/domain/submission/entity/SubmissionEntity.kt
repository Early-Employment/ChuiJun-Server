package team.joup.chuijun.domain.submission.entity

import team.joup.chuijun.domain.assignment.entity.AssignmentEntity
import team.joup.chuijun.domain.member.entity.MemberEntity
import team.joup.chuijun.domain.problem.entity.ProblemEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "submissions")
class SubmissionEntity(
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

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type", nullable = false)
    var executionType: ExecutionType = ExecutionType.SUBMIT,

    @Column(name = "language_code", nullable = false, length = 40)
    var languageCode: String,

    @Column(name = "submitted_code", nullable = false, columnDefinition = "MEDIUMTEXT")
    var submittedCode: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "judge_status", nullable = false)
    var judgeStatus: JudgeStatus = JudgeStatus.QUEUED,

    @Column(nullable = false)
    var score: Int = 0,

    @Column(name = "study_seconds")
    var studySeconds: Int? = null,

    @Column(name = "view_count", nullable = false)
    var viewCount: Int = 0,

    @Column(name = "submitted_at", nullable = false, updatable = false)
    val submittedAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "judged_at")
    var judgedAt: LocalDateTime? = null
)
