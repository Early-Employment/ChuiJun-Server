package team.joup.chuijun.domain.problem.entity

import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "problems")
class ProblemJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    var writer: MemberJpaEntity? = null,

    @Column(name = "problem_code", nullable = false, unique = true, length = 120)
    var problemCode: String,

    @Column(nullable = false, length = 200)
    var title: String,

    @Column(name = "description_md", nullable = false, columnDefinition = "TEXT")
    var descriptionMd: String,

    @Column(name = "input_md", columnDefinition = "TEXT")
    var inputMd: String? = null,

    @Column(name = "output_md", columnDefinition = "TEXT")
    var outputMd: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var level: ProblemLevel,

    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm_type", length = 30)
    var algorithmType: AlgorithmType? = null,

    @Column(name = "primary_tag", length = 50)
    var primaryTag: String? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tag_list_json")
    var tagListJson: String? = null,

    @Column(name = "time_limit_ms", nullable = false)
    var timeLimitMs: Int = 1000,

    @Column(name = "memory_limit_kb", nullable = false)
    var memoryLimitKb: Int = 262144,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProblemStatus = ProblemStatus.DRAFT,

    @Column(name = "submit_count", nullable = false)
    var submitCount: Int = 0,

    @Column(name = "accepted_count", nullable = false)
    var acceptedCount: Int = 0,

    @Version
    val version: Long = 0L,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {

    val point: Int
        get() = level.score

    fun increaseSubmitCount() {
        this.submitCount++
        this.updatedAt = LocalDateTime.now()
    }

    fun increaseAcceptedCount() {
        this.acceptedCount++
        this.updatedAt = LocalDateTime.now()
    }

    fun getAcceptRate(): Double {
        if (submitCount <= 0) return 0.0
        val roundedRate = kotlin.math.round((acceptedCount.toDouble() / submitCount) * 100 * 10) / 10.0
        return roundedRate.coerceIn(0.0, 100.0)
    }
}
