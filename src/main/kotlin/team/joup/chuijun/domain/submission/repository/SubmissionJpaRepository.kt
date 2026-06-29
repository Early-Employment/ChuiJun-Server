package team.joup.chuijun.domain.submission.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.entity.SubmissionJpaEntity

interface SubmissionJpaRepository : JpaRepository<SubmissionJpaEntity, Long> {

    @Query("SELECT s FROM SubmissionJpaEntity s LEFT JOIN FETCH s.problem WHERE s.member.id = :memberId")
    fun findByMemberId(
        @Param("memberId") memberId: Long,
        pageable: Pageable
    ): Page<SubmissionJpaEntity>

    fun existsByMemberIdAndProblemIdAndJudgeStatusIn(
        memberId: Long,
        problemId: Long,
        judgeStatuses: Collection<JudgeStatus>
    ): Boolean
}
