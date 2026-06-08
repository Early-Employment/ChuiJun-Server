package team.joup.chuijun.domain.assignment.entity

import team.joup.chuijun.domain.course.entity.CourseEntity
import team.joup.chuijun.domain.member.entity.MemberEntity
import team.joup.chuijun.domain.problem.entity.ProblemEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assignments")
class AssignmentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    var course: CourseEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: ProblemEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    var teacher: MemberEntity,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(name = "max_score", nullable = false)
    var maxScore: Int = 100,

    @Column(name = "deadline_at")
    var deadlineAt: LocalDateTime? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AssignmentStatus = AssignmentStatus.OPEN,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
