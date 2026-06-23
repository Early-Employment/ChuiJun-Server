package team.joup.chuijun.domain.problem.entity

import jakarta.persistence.*

@Entity
@Table(name = "test_cases")
class TestCaseJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: ProblemJpaEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    var caseType: CaseType = CaseType.HIDDEN,

    @Column(name = "input_text", nullable = false, columnDefinition = "TEXT")
    var inputText: String,

    @Column(name = "expected_output_text", nullable = false, columnDefinition = "TEXT")
    var expectedOutputText: String,

    @Column(name = "explanation_md", columnDefinition = "TEXT")
    var explanationMd: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 1,

    @Column(name = "is_enabled", nullable = false)
    var isEnabled: Boolean = true
)
