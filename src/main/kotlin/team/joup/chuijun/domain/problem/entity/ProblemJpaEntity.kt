package team.joup.chuijun.domain.problem.entity

import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import jakarta.persistence.*
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

    @Column(name = "description_md", nullable = false, columnDefinition = "MEDIUMTEXT")
    var descriptionMd: String,

    @Column(name = "input_md", columnDefinition = "TEXT")
    var inputMd: String? = null,

    @Column(name = "output_md", columnDefinition = "TEXT")
    var outputMd: String? = null,

    @Column(nullable = false)
    var level: Byte,

    @Column(name = "primary_tag", length = 50)
    var primaryTag: String? = null,

    @Column(name = "tag_list_json", columnDefinition = "JSON")
    var tagListJson: String? = null,

    @Column(nullable = false)
    var point: Int = 10,

    @Column(name = "time_limit_ms", nullable = false)
    var timeLimitMs: Int = 1000,

    @Column(name = "memory_limit_kb", nullable = false)
    var memoryLimitKb: Int = 262144,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProblemStatus = ProblemStatus.DRAFT,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
