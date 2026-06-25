package team.joup.chuijun.domain.problem.dto.request

import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.ProblemStatus

data class UpdateProblemRequest(
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
    val testCases: List<UpdateTestCaseRequest>
)

data class UpdateTestCaseRequest(
    val caseType: CaseType,
    val inputText: String,
    val expectedOutputText: String,
    val explanationMd: String?,
    val sortOrder: Int,
    val isEnabled: Boolean
)
