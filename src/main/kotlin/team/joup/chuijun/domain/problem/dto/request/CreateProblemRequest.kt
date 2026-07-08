package team.joup.chuijun.domain.problem.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import team.joup.chuijun.domain.problem.entity.AlgorithmType
import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.ProblemLevel
import team.joup.chuijun.domain.problem.entity.ProblemStatus

data class CreateProblemRequest(
    @field:NotBlank(message = "문제 코드는 필수입니다.")
    @field:Size(max = 120, message = "문제 코드는 120자를 초과할 수 없습니다.")
    val problemCode: String,

    @field:NotBlank(message = "문제 제목은 필수입니다.")
    @field:Size(max = 200, message = "문제 제목은 200자를 초과할 수 없습니다.")
    val title: String,

    @field:NotBlank(message = "문제 설명은 필수입니다.")
    val descriptionMd: String,

    val inputMd: String?,
    val outputMd: String?,
    val level: ProblemLevel,
    val algorithmType: AlgorithmType? = null,
    val primaryTag: String?,
    val tagListJson: String?,
    val point: Int,
    val timeLimitMs: Int,
    val memoryLimitKb: Int,
    val status: ProblemStatus,

    @field:NotEmpty(message = "최소 하나 이상의 테스트 케이스가 필요합니다.")
    @field:Valid
    val testCases: List<CreateTestCaseRequest>
)

data class CreateTestCaseRequest(
    val caseType: CaseType,
    @field:NotBlank(message = "테스트 케이스 입력 텍스트는 필수입니다.")
    val inputText: String,
    @field:NotBlank(message = "테스트 케이스 예상 출력 텍스트는 필수입니다.")
    val expectedOutputText: String,
    val explanationMd: String?,
    val sortOrder: Int
)
