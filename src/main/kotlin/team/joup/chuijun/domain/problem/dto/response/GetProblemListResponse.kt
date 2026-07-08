package team.joup.chuijun.domain.problem.dto.response

import team.joup.chuijun.domain.problem.entity.AlgorithmType
import team.joup.chuijun.domain.problem.entity.ProblemLevel
import team.joup.chuijun.domain.problem.entity.SolveStatus

data class GetProblemListResponse(
    val problemId: Long,
    val problemCode: String,
    val title: String,
    val level: ProblemLevel,
    val algorithmType: AlgorithmType?,
    val solveStatus: SolveStatus?,
    val point: Int,
    val acceptRate: Double
)
