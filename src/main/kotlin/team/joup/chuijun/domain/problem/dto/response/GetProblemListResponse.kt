package team.joup.chuijun.domain.problem.dto.response

import team.joup.chuijun.domain.problem.entity.ProblemLevel

data class GetProblemListResponse(
    val problemId: Long,
    val problemCode: String,
    val title: String,
    val level: ProblemLevel,
    val primaryTag: String?,
    val point: Int,
    val acceptRate: Double
)
