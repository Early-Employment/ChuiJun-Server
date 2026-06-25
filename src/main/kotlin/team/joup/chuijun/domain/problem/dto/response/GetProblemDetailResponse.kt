package team.joup.chuijun.domain.problem.dto.response

data class GetProblemDetailResponse(
    val problemId: Long,
    val problemCode: String,
    val title: String,
    val descriptionMd: String,
    val inputMd: String?,
    val outputMd: String?,
    val level: Byte,
    val primaryTag: String?,
    val point: Int,
    val timeLimitMs: Int,
    val memoryLimitKb: Int,
    val testCases: List<TestCaseDto>,
    val acceptRate: Double
)

data class TestCaseDto(
    val testCaseId: Long,
    val caseType: String,
    val inputText: String,
    val expectedOutputText: String,
    val explanationMd: String?
)
