package team.joup.chuijun.domain.submission.dto.request

import team.joup.chuijun.domain.submission.entity.JudgeStatus

data class SubmitProblemRequest(
    val memberId: Long,
    val problemId: Long,
    val languageCode: String,
    val submittedCode: String,
    val judgeStatus: JudgeStatus,
    val score: Int,
    val studySeconds: Int?
)
