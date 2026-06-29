package team.joup.chuijun.domain.submission.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.entity.SubmissionJpaEntity

interface SubmissionJpaRepository : JpaRepository<SubmissionJpaEntity, Long> {

    fun existsByMemberIdAndProblemIdAndJudgeStatusIn(
        memberId: Long,
        problemId: Long,
        judgeStatuses: Collection<JudgeStatus>
    ): Boolean
}
