package team.joup.chuijun.domain.problem.dto.request

import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.ProblemStatus

data class CreateProblemRequest(
    val problemCode: String,
    val title: String,
    val descriptionMd: String,
    val inputMd: String?,
    val outputMd: String?,
    val level: Byte,
    val primaryTag: String?,
    val tagListJson: String?,
    val point: Int,
    val timeLimitMs: Int,
    val memoryLimitKb: Int,
    val status: ProblemStatus,
    val testCases: List<CreateTestCaseRequest>
)

data class CreateTestCaseRequest(
    val caseType: CaseType,
    val inputText: String,
    val expectedOutputText: String,
    val explanationMd: String?,
    val sortOrder: Int
)
