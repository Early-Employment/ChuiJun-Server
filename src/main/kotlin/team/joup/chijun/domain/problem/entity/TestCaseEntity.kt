package team.joup.chijun.domain.problem.entity

import jakarta.persistence.*

enum class CaseType { PUBLIC, HIDDEN }

@Entity
@Table(name = "test_cases")
class TestCaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    var problem: ProblemEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    var caseType: CaseType = CaseType.HIDDEN,

    @Column(name = "input_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    var inputText: String,

    @Column(name = "expected_output_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    var expectedOutputText: String,

    @Column(name = "explanation_md", columnDefinition = "TEXT")
    var explanationMd: String? = null,

    @Column(name = "sort_order", nullable = false)
    var sortOrder: Int = 1,

    @Column(name = "is_enabled", nullable = false)
    var isEnabled: Boolean = true
)