package team.joup.chuijun.domain.classroom.entity

import jakarta.persistence.*
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity
import java.time.LocalDateTime

@Entity
@Table(name = "classroom_assignments")
class ClassroomAssignmentJpaEntity(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    val classroom: ClassroomJpaEntity,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    val problem: ProblemJpaEntity,

    @Column(nullable = false)
    var dueDate: LocalDateTime,

    @Column(nullable = false)
    var isRequired: Boolean = true
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}
