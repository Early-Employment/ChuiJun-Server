package team.joup.chuijun.domain.problem.dto.response

data class GetProblemListResponse(
    val problemId: Long,
    val problemCode: String,
    val title: String,
    val level: Byte,
    val primaryTag: String?,
    val point: Int,
    val acceptRate: Double
)
