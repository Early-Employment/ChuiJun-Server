package team.joup.chuijun.domain.submission.dto.response

import team.joup.chuijun.domain.submission.entity.JudgeStatus

data class SubmitProblemResponse(
    val submissionId: Long,
    val judgeStatus: JudgeStatus,
    val score: Int,
    val totalScoreAfter: Int
)
